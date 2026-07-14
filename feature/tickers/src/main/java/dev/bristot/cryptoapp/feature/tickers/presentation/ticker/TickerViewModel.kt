package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
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
) : ViewModel() {

    private val tickerStateFlow = MutableStateFlow<TickerState>(value = TickerState.Initial)
    private val quoteCurrencyFlow = MutableStateFlow(
        settingsRepository.settings.value.selectedQuoteCurrency
    )
    private var lastRequestedSettings: AppSettings? = null
    private var refreshJob: Job? = null

    val state: StateFlow<TickerState> = tickerStateFlow.asStateFlow()
    val quoteCurrency: StateFlow<QuoteCurrency> = quoteCurrencyFlow.asStateFlow()

    fun refreshIfNeeded() {
        val settings = settingsRepository.settings.value
        val hasUsableResult = tickerStateFlow.value is TickerState.Success
        if (settings == lastRequestedSettings &&
            (refreshJob?.isActive == true || hasUsableResult)
        ) return

        lastRequestedSettings = settings
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            quoteCurrencyFlow.value = settings.selectedQuoteCurrency
            tickerStateFlow.update { TickerState.Loading }
            tickersRepository.getTicker(
                coinId = coinId,
                currencies = settings.requestedQuoteCurrencies,
            ).flowOn(dispatcherProvider.default).catch { error ->
                tickerStateFlow.update {
                    TickerState.Error(error = error.message ?: "Unexpected error")
                }
            }.collect { ticker ->
                tickerStateFlow.update {
                    TickerState.Success(ticker = ticker)
                }
            }
        }
    }
}
