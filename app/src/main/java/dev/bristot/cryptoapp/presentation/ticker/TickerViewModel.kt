package dev.bristot.cryptoapp.presentation.ticker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.repository.TickersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = TickerModule.TickerViewModelFactory::class)
class TickerViewModel @AssistedInject constructor(
    @Assisted private val coinId: String,
    private val dispatcherProvider: DispatcherProvider,
    private val tickersRepository: TickersRepository,
) : ViewModel() {

    private val tickerStateFlow = MutableStateFlow<TickerState>(value = TickerState.Initial)

    val state: StateFlow<TickerState>
        get() = tickerStateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = TickerState.Initial,
        )

    fun getTicker() {
        viewModelScope.launch {
            tickerStateFlow.update { TickerState.Loading }
            tickersRepository.getTicker(
                coinId = coinId, currencies = setOf(
                    CurrencySymbol.BRL
                )
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