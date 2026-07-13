package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
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
class CoinListViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val coinRepository: CoinRepository,
    private val sortTemplate: SortTemplate<Coin>,
) : ViewModel() {

    private val coinStateFlow = MutableStateFlow<CoinListState>(CoinListState.Initial)

    val state: StateFlow<CoinListState>
        get() = coinStateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = CoinListState.Initial,
        )

    init {
        fetchCoins()
    }

    fun handleToTop(shouldShow: Boolean) {
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties ?: return
        coinStateFlow.update { current.copy(toTopVisibility = shouldShow) }
    }

    fun sortBy(sortState: SortState) {
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties ?: return
        coinStateFlow.update {
            current.copy(coins = sortTemplate.sort(current.coins, sortState))
        }
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            coinStateFlow.update { CoinListState.Loading }
            coinRepository.getCoins()
                .flowOn(dispatcherProvider.default)
                .catch { coinStateFlow.update { CoinListState.Error("An error occurred") } }
                .collect { coins ->
                    coinStateFlow.update {
                        CoinListState.SuccessWithUIProperties(
                            coins = sortTemplate.sort(coins, SortState()),
                        )
                    }
                }
        }
    }
}
