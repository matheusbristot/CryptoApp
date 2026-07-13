package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickersViewModel @Inject constructor(
    private val tickersRepository: TickersRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val sortTemplate: SortTemplate<Ticker>,
) : ViewModel() {

    private val tickersStateFlow = MutableStateFlow<TickersState>(value = TickersState.Initial)

    val state: StateFlow<TickersState>
        get() = tickersStateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = TickersState.Initial,
        )

    init {
        getTickers()
    }

    fun getTickers() {
        viewModelScope.launch {
            tickersStateFlow.update { TickersState.Loading }
            tickersRepository.getTickers(currencies = setOf(CurrencySymbol.BRL))
                .flowOn(dispatcherProvider.default).catch { error ->
                    tickersStateFlow.update {
                        TickersState.Error(error = error.message ?: "Unexpected error")
                    }
                }.collect { tickers ->
                    tickersStateFlow.update {
                        TickersState.Success(tickers = sortTemplate.sort(tickers, SortState()))
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
