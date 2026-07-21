package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = TickerModule.TickerViewModelFactory::class)
class TickerViewModel @AssistedInject constructor(
    @Assisted private val coinId: String,
    private val dispatcherProvider: DispatcherProvider,
    private val tickersRepository: TickersRepository,
    private val settingsRepository: SettingsRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val tickerStateFlow = MutableStateFlow<TickerState>(value = TickerState.Initial)
    private val quoteCurrencyFlow = MutableStateFlow(
        settingsRepository.settings.value.selectedQuoteCurrency
    )
    private val isFavoriteFlow = MutableStateFlow(false)
    private var observedSettings: AppSettings? = null
    private var observationJob: Job? = null
    private var refreshJob: Job? = null

    val state: StateFlow<TickerState> = tickerStateFlow.asStateFlow()
    val quoteCurrency: StateFlow<QuoteCurrency> = quoteCurrencyFlow.asStateFlow()
    val isFavorite: StateFlow<Boolean> = isFavoriteFlow.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesRepository.observeIsFavorite(FavoriteType.TICKER, coinId)
                .collect { favorite -> isFavoriteFlow.value = favorite }
        }
    }

    fun refreshIfNeeded() {
        val settings = settingsRepository.settings.value
        quoteCurrencyFlow.value = settings.selectedQuoteCurrency
        if (settings != observedSettings) {
            observedSettings = settings
            observationJob?.cancel()
            tickerStateFlow.value = TickerState.Loading
            observationJob = viewModelScope.launch {
                tickersRepository.observeTicker(
                    coinId = coinId,
                    currencies = settings.requestedQuoteCurrencies,
                ).flowOn(dispatcherProvider.default).catch { error ->
                    if (tickerStateFlow.value !is TickerState.Success) {
                        tickerStateFlow.value = TickerState.Error(
                            error = error.message ?: "Unexpected error",
                        )
                    }
                }.collect { ticker ->
                    if (ticker != null) {
                        tickerStateFlow.value = TickerState.Success(ticker)
                    }
                }
            }
        }

        if (refreshJob?.isActive == true) return
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(dispatcherProvider.default) {
            try {
                tickersRepository.refreshTicker(
                    coinId = coinId,
                    currencies = settings.requestedQuoteCurrencies,
                    force = false,
                )
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (error: Exception) {
                if (tickerStateFlow.value !is TickerState.Success) {
                    tickerStateFlow.update {
                        TickerState.Error(error = error.message ?: "Unexpected error")
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val shouldFavorite = !isFavoriteFlow.value
        viewModelScope.launch(dispatcherProvider.default) {
            favoritesRepository.setFavorite(
                type = FavoriteType.TICKER,
                itemId = coinId,
                isFavorite = shouldFavorite,
            )
        }
    }
}
