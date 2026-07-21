package dev.bristot.cryptoapp.feature.coins.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.CoinQuote
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CoinDetailModule.CoinDetailViewModelFactory::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CoinDetailViewModel @AssistedInject constructor(
    @Assisted private val coinId: String,
    private val dispatcherProvider: DispatcherProvider,
    private val coinRepository: CoinRepository,
    private val tickersRepository: TickersRepository,
    private val settingsRepository: SettingsRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow(
        CoinDetailState(quoteCurrency = settingsRepository.settings.value.selectedQuoteCurrency),
    )
    private var refreshJob: Job? = null
    private var lastRequestedSettings: AppSettings? = null

    val state: StateFlow<CoinDetailState> = mutableState.asStateFlow()

    init {
        observeCachedData()
    }

    fun refreshIfNeeded() {
        val settings = settingsRepository.settings.value
        val currentState = mutableState.value
        val hasCompleteResult = currentState.coin?.quote?.price != null &&
            currentState.errorMessage == null
        if (settings == lastRequestedSettings &&
            (refreshJob?.isActive == true || hasCompleteResult)
        ) return

        lastRequestedSettings = settings
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(dispatcherProvider.io) {
            mutableState.update {
                it.copy(
                    quoteCurrency = settings.selectedQuoteCurrency,
                    isLoading = it.coin == null,
                    errorMessage = null,
                )
            }

            val errors = buildList {
                refreshCatching { coinRepository.refreshCoin(coinId, force = false) }
                    ?.let(::add)
                refreshCatching {
                    tickersRepository.refreshTicker(
                        coinId = coinId,
                        currencies = settings.requestedQuoteCurrencies,
                        force = false,
                    )
                }?.let(::add)
            }
            if (errors.isNotEmpty()) {
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errors.first().message ?: "Unable to update this asset",
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val favorite = mutableState.value.isFavorite
        viewModelScope.launch(dispatcherProvider.io) {
            favoritesRepository.setFavorite(
                type = FavoriteType.COIN,
                itemId = coinId,
                isFavorite = !favorite,
            )
        }
    }

    private fun observeCachedData() {
        viewModelScope.launch {
            combine(
                settingsRepository.settings.flatMapLatest { settings ->
                    combine(
                        coinRepository.observeCoin(coinId),
                        tickersRepository.observeTicker(
                            coinId = coinId,
                            currencies = settings.requestedQuoteCurrencies,
                        ),
                    ) { coin, ticker ->
                        val quotedCoin = coin?.copy(
                            quote = CoinQuote(
                                currency = settings.selectedQuoteCurrency,
                                price = ticker?.prices?.get(settings.selectedQuoteCurrency)?.price,
                            ),
                        )
                        settings to quotedCoin
                    }
                },
                favoritesRepository.observeIsFavorite(FavoriteType.COIN, coinId),
            ) { (settings, coin), favorite ->
                val hasSelectedQuote = coin?.quote?.price != null
                CoinDetailState(
                    coin = coin,
                    quoteCurrency = settings.selectedQuoteCurrency,
                    isFavorite = favorite,
                    isLoading = coin == null && mutableState.value.errorMessage == null,
                    errorMessage = if (hasSelectedQuote) null else mutableState.value.errorMessage,
                )
            }
                .catch { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Unexpected error")
                    }
                }
                .collect { state -> mutableState.value = state }
        }
    }

    private suspend fun refreshCatching(block: suspend () -> Unit): Throwable? =
        try {
            block()
            null
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            error
        }
}
