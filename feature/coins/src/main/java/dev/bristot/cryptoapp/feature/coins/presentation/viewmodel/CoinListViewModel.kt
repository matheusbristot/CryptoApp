package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.usecase.GetQuotedCoinsUseCase
import dev.bristot.cryptoapp.feature.coins.domain.entity.CoinQuote
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val getQuotedCoins: GetQuotedCoinsUseCase,
    private val sortTemplate: SortTemplate<Coin>,
    private val settingsRepository: SettingsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val coinRepository: CoinRepository,
    private val tickersRepository: TickersRepository,
) : ViewModel() {

    private val coinStateFlow = MutableStateFlow<CoinListState>(CoinListState.Initial)
    private var lastRequestedQuote: QuoteCurrency? = null
    private var refreshJob: Job? = null
    private var favoriteRefreshJob: Job? = null
    private var currentSortState = SortState()
    private var favoriteBaselineObserved = false
    private var previousFavoriteIds = emptySet<String>()
    private var isScreenActive = false

    private val favoritesFlow = MutableStateFlow<List<FavoriteCoinItem>>(emptyList())
    private val selectedSectionFlow = MutableStateFlow(CoinListSection.ALL)

    val state: StateFlow<CoinListState> = coinStateFlow.asStateFlow()
    val favorites: StateFlow<List<FavoriteCoinItem>> = favoritesFlow.asStateFlow()
    val selectedSection: StateFlow<CoinListSection> = selectedSectionFlow.asStateFlow()

    init {
        observeFavorites()
    }

    fun handleToTop(shouldShow: Boolean) {
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties ?: return
        coinStateFlow.update { current.copy(toTopVisibility = shouldShow) }
    }

    fun sortBy(sortState: SortState) {
        currentSortState = sortState
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties
        if (current != null) {
            coinStateFlow.update {
                current.copy(coins = sortTemplate.sort(current.coins, sortState))
            }
        }
        favoritesFlow.update(::sortFavoriteItems)
    }

    fun selectSection(section: CoinListSection) {
        if (section == CoinListSection.FAVORITES && favoritesFlow.value.isEmpty()) return
        selectedSectionFlow.value = section
    }

    fun setActive(active: Boolean) {
        isScreenActive = active
        if (active) refreshFavoritesIfNeeded()
    }

    fun refreshIfNeeded() {
        refreshFavoritesIfNeeded()
        val selectedQuote = settingsRepository.settings.value.selectedQuoteCurrency
        val hasUsableResult = coinStateFlow.value is CoinListState.SuccessWithUIProperties
        if (selectedQuote == lastRequestedQuote &&
            (refreshJob?.isActive == true || hasUsableResult)
        ) return

        lastRequestedQuote = selectedQuote
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            coinStateFlow.update { CoinListState.Loading }
            try {
                val coins = getQuotedCoins(selectedQuote)
                coinStateFlow.update {
                    CoinListState.SuccessWithUIProperties(
                        coins = sortTemplate.sort(coins, currentSortState),
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                coinStateFlow.update { CoinListState.Error("An error occurred") }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.observeFavorites(FavoriteType.COIN)
                .flatMapLatest { refs ->
                    updateFavoriteSelection(refs.map { it.itemId }.toSet())
                    if (refs.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        settingsRepository.settings.flatMapLatest { settings ->
                            combine(
                                refs.map { ref ->
                                    combine(
                                        coinRepository.observeCoin(ref.itemId),
                                        tickersRepository.observeTicker(
                                            ref.itemId,
                                            settings.requestedQuoteCurrencies,
                                        ),
                                    ) { coin, ticker ->
                                        FavoriteCoinItem(
                                            id = ref.itemId,
                                            coin = coin?.copy(
                                                quote = CoinQuote(
                                                    currency = settings.selectedQuoteCurrency,
                                                    price = ticker?.prices
                                                        ?.get(settings.selectedQuoteCurrency)
                                                        ?.price,
                                                ),
                                            ),
                                        )
                                    }
                                },
                            ) { items -> items.toList() }
                        }
                    }
                }
                .collect { items -> favoritesFlow.value = sortFavoriteItems(items) }
        }
    }

    private fun updateFavoriteSelection(ids: Set<String>) {
        if (!favoriteBaselineObserved) {
            favoriteBaselineObserved = true
        } else if (ids.size > previousFavoriteIds.size && ids.any { it !in previousFavoriteIds }) {
            selectedSectionFlow.value = CoinListSection.FAVORITES
        }
        if (ids.isEmpty()) selectedSectionFlow.value = CoinListSection.ALL
        previousFavoriteIds = ids
        if (isScreenActive && ids.isNotEmpty()) refreshFavoritesIfNeeded()
    }

    private fun refreshFavoritesIfNeeded() {
        val ids = previousFavoriteIds
        if (ids.isEmpty()) return
        val settings = settingsRepository.settings.value
        favoriteRefreshJob?.cancel()
        favoriteRefreshJob = viewModelScope.launch(dispatcherProvider.io) {
            ids.forEach { id ->
                refreshFavoriteCatching { coinRepository.refreshCoin(id, force = false) }
                refreshFavoriteCatching {
                    tickersRepository.refreshTicker(
                        coinId = id,
                        currencies = settings.requestedQuoteCurrencies,
                        force = false,
                    )
                }
            }
        }
    }

    private suspend fun refreshFavoriteCatching(block: suspend () -> Unit) {
        try {
            block()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            // Cache refresh is best effort; observed cached data remains visible.
        }
    }

    private fun sortFavoriteItems(items: List<FavoriteCoinItem>): List<FavoriteCoinItem> {
        val loadedById = items.mapNotNull { item -> item.coin?.let { item.id to it } }.toMap()
        val sortedLoadedIds = sortTemplate.sort(loadedById.values.toList(), currentSortState)
            .map(Coin::id)
        val order = sortedLoadedIds.withIndex().associate { (index, id) -> id to index }
        return items.sortedWith(
            compareBy<FavoriteCoinItem> { it.coin == null }
                .thenBy { order[it.id] ?: Int.MAX_VALUE },
        )
    }
}
