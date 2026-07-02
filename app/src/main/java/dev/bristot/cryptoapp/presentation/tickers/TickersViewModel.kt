package dev.bristot.cryptoapp.presentation.tickers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.domain.repository.TickersRepository
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TickersViewModel @Inject constructor(
    private val tickersRepository: TickersRepository,
    private val dispatcherProvider: DispatcherProvider
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
                        TickersState.Success(tickers = tickers)
                    }
                }
        }
    }

    fun sortBy(sortType: SortType, sortOrder: SortOrder) {
        require(tickersStateFlow.value is TickersState.Success, lazyMessage = {
            "IllegalArgument: ${tickersStateFlow.value}, use a TickersState.Success instead of"
        })

        val state = (tickersStateFlow.value as TickersState.Success)
        viewModelScope.launch {
            val sortFlow: Flow<List<Ticker>> = flowOf(
                sortTickers(
                    tickers = state.tickers,
                    sortType = sortType,
                    sortOrder = sortOrder,
                )
            )
            sortFlow.flowOn(dispatcherProvider.default).catch {
                tickersStateFlow.update {
                    TickersState.Error(error = "Unexpected error: when sort")
                }
            }.collect { newList ->
                tickersStateFlow.update {
                    state.copy(tickers = newList)
                }
            }
        }
    }
}
