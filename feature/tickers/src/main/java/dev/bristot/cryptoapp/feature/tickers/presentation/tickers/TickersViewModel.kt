package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickersViewModel @Inject constructor(
    private val tickersRepository: TickersRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val sortTemplate: SortTemplate<Ticker>,
    private val settingsRepository: SettingsRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val tickersStateFlow = MutableStateFlow<TickersState>(value = TickersState.Initial)
    private val quoteCurrencyFlow = MutableStateFlow(
        settingsRepository.settings.value.selectedQuoteCurrency
    )
    private var lastRequestedSettings: AppSettings? = null
    private var refreshJob: Job? = null
    private var favoriteRefreshJob: Job? = null
    private var screenActive = false
    private var previousFavoriteIds: Set<String>? = null
    private val favoriteRefsFlow = MutableStateFlow<List<FavoriteRef>>(emptyList())
    private val favoriteRefreshErrorsFlow = MutableStateFlow<Map<String, String>>(emptyMap())
    private val sortStateFlow = MutableStateFlow(SortState())
    private val selectedSectionFlow = MutableStateFlow(TickersSection.MARKET)
    private val favoritesStateFlow = MutableStateFlow(TickerFavoritesState())

    val state: StateFlow<TickersState> = tickersStateFlow.asStateFlow()
    val quoteCurrency: StateFlow<QuoteCurrency> = quoteCurrencyFlow.asStateFlow()
    val favoritesState: StateFlow<TickerFavoritesState> = favoritesStateFlow.asStateFlow()
    val selectedSection: StateFlow<TickersSection> = selectedSectionFlow.asStateFlow()

    init {
        observeFavoriteRefs()
        observeFavoriteTickers()
    }

    fun refreshIfNeeded() {
        val settings = settingsRepository.settings.value
        refreshFavorites(settings, favoriteRefsFlow.value)
        val hasUsableResult = tickersStateFlow.value is TickersState.Success
        if (settings == lastRequestedSettings &&
            (refreshJob?.isActive == true || hasUsableResult)
        ) return

        lastRequestedSettings = settings
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            quoteCurrencyFlow.value = settings.selectedQuoteCurrency
            tickersStateFlow.update { TickersState.Loading }
            tickersRepository.getTickers(currencies = settings.requestedQuoteCurrencies)
                .flowOn(dispatcherProvider.default)
                .catch { error ->
                    tickersStateFlow.update {
                        TickersState.Error(error = error.message ?: "Unexpected error")
                    }
                }
                .collect { tickers ->
                    val displayableTickers = tickers.filter { ticker ->
                        settings.selectedQuoteCurrency in ticker.prices
                    }
                    tickersStateFlow.update {
                        TickersState.Success(
                            tickers = sortTemplate.sort(displayableTickers, SortState())
                        )
                    }
                }
        }
    }

    fun sortBy(sortState: SortState) {
        sortStateFlow.value = sortState
        val state = tickersStateFlow.value as? TickersState.Success ?: return
        viewModelScope.launch(dispatcherProvider.default) {
            val sortedTickers = sortTemplate.sort(state.tickers, sortState)
            tickersStateFlow.update {
                state.copy(tickers = sortedTickers)
            }
        }
    }

    fun selectSection(section: TickersSection) {
        selectedSectionFlow.value = if (
            section == TickersSection.FAVORITES && favoriteRefsFlow.value.isEmpty()
        ) {
            TickersSection.MARKET
        } else {
            section
        }
    }

    fun setActive(active: Boolean) {
        screenActive = active
        if (active) {
            refreshFavorites(settingsRepository.settings.value, favoriteRefsFlow.value)
        } else {
            favoriteRefreshJob?.cancel()
        }
    }

    private fun observeFavoriteRefs() {
        viewModelScope.launch {
            favoritesRepository.observeFavorites(FavoriteType.TICKER).collect { refs ->
                val ids = refs.mapTo(linkedSetOf()) { ref -> ref.itemId }
                val previousIds = previousFavoriteIds
                if (previousIds != null && (ids - previousIds).isNotEmpty()) {
                    selectedSectionFlow.value = TickersSection.FAVORITES
                }
                if (ids.isEmpty()) {
                    selectedSectionFlow.value = TickersSection.MARKET
                }
                previousFavoriteIds = ids
                favoriteRefsFlow.value = refs
                if (screenActive) {
                    refreshFavorites(settingsRepository.settings.value, refs)
                }
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeFavoriteTickers() {
        viewModelScope.launch {
            combine(
                favoriteRefsFlow,
                settingsRepository.settings,
                sortStateFlow,
                favoriteRefreshErrorsFlow,
            ) { refs, settings, sortState, errors ->
                FavoriteObservationArgs(refs, settings, sortState, errors)
            }.flatMapLatest { args ->
                    val refs = args.refs
                    val settings = args.settings
                    if (refs.isEmpty()) {
                        flowOf(TickerFavoritesState())
                    } else {
                        combine(
                            refs.map { ref ->
                                tickersRepository.observeTicker(
                                    coinId = ref.itemId,
                                    currencies = settings.requestedQuoteCurrencies,
                                ).map { ticker ->
                                    val displayTicker = ticker?.takeIf { candidate ->
                                        settings.selectedQuoteCurrency in candidate.prices
                                    }
                                    val missingQuote = ticker != null && displayTicker == null
                                    FavoriteTickerState(
                                        ref = ref,
                                        ticker = displayTicker,
                                        isLoading = ticker == null && args.errors[ref.itemId] == null,
                                        error = args.errors[ref.itemId] ?: if (missingQuote) {
                                            "Quote ${settings.selectedQuoteCurrency.name} unavailable"
                                        } else {
                                            null
                                        },
                                    )
                                }.onStart {
                                    emit(
                                        FavoriteTickerState(
                                            ref = ref,
                                            isLoading = true,
                                        ),
                                    )
                                }.catch { error ->
                                    emit(
                                        FavoriteTickerState(
                                            ref = ref,
                                            error = error.message ?: "Unexpected error",
                                        ),
                                    )
                                }
                            },
                        ) { observedItems ->
                            val itemsById = observedItems.associateBy { item -> item.ref.itemId }
                            val loadedItems = sortTemplate.sort(
                                observedItems.mapNotNull(FavoriteTickerState::ticker),
                                args.sortState,
                            ).mapNotNull { ticker -> itemsById[ticker.id] }
                            val unavailableItems = refs.mapNotNull { ref ->
                                itemsById[ref.itemId]?.takeIf { item -> item.ticker == null }
                            }
                            TickerFavoritesState(items = loadedItems + unavailableItems)
                        }
                    }
                }
                .catch { error ->
                    favoritesStateFlow.value = TickerFavoritesState(
                        items = favoriteRefsFlow.value.map { ref ->
                            FavoriteTickerState(
                                ref = ref,
                                error = error.message ?: "Unexpected error",
                            )
                        },
                    )
                }
                .collect { state -> favoritesStateFlow.value = state }
        }
    }

    private fun refreshFavorites(settings: AppSettings, refs: List<FavoriteRef>) {
        favoriteRefreshJob?.cancel()
        favoriteRefreshJob = viewModelScope.launch(dispatcherProvider.default) {
            favoriteRefreshErrorsFlow.value = emptyMap()
            refs.forEach { ref ->
                try {
                    tickersRepository.refreshTicker(
                        coinId = ref.itemId,
                        currencies = settings.requestedQuoteCurrencies,
                        force = false,
                    )
                } catch (exception: kotlinx.coroutines.CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    favoriteRefreshErrorsFlow.update { errors ->
                        errors + (ref.itemId to (exception.message ?: "Unexpected error"))
                    }
                }
            }
        }
    }

    private data class FavoriteObservationArgs(
        val refs: List<FavoriteRef>,
        val settings: AppSettings,
        val sortState: SortState,
        val errors: Map<String, String>,
    )
}
