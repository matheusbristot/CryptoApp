package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickersViewModel @Inject constructor(
    private val tickersRepository: TickersRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val sortTemplate: SortTemplate<Ticker>,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val tickersStateFlow = MutableStateFlow<TickersState>(value = TickersState.Initial)
    private val quoteCurrencyFlow = MutableStateFlow(
        settingsRepository.settings.value.selectedQuoteCurrency
    )
    private var lastRequestedSettings: AppSettings? = null
    private var refreshJob: Job? = null

    val state: StateFlow<TickersState> = tickersStateFlow.asStateFlow()
    val quoteCurrency: StateFlow<QuoteCurrency> = quoteCurrencyFlow.asStateFlow()

    fun refreshIfNeeded() {
        val settings = settingsRepository.settings.value
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
        require(tickersStateFlow.value is TickersState.Success, lazyMessage = {
            "IllegalArgument: ${tickersStateFlow.value}, use a TickersState.Success instead of"
        })

        val state = (tickersStateFlow.value as TickersState.Success)
        viewModelScope.launch(dispatcherProvider.default) {
            val sortedTickers = sortTemplate.sort(state.tickers, sortState)
            tickersStateFlow.update {
                state.copy(tickers = sortedTickers)
            }
        }
    }
}
