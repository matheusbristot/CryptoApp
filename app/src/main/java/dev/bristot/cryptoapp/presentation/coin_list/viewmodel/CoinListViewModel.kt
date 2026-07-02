package dev.bristot.cryptoapp.presentation.coin_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.domain.entity.Coin
import dev.bristot.cryptoapp.domain.repository.CoinRepository
import dev.bristot.cryptoapp.presentation.coin_list.SortOrder
import dev.bristot.cryptoapp.presentation.coin_list.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinListViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val coinRepository: CoinRepository,
) : ViewModel() {

    private val coinStateFlow = MutableStateFlow<CoinListState>(value = CoinListState.Initial)

    val state: StateFlow<CoinListState>
        get() = coinStateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = CoinListState.Initial,
        )

    init {
        fetchCoins()
    }

    fun changeSort(sortOrder: SortOrder) {
        require(coinStateFlow.value is CoinListState.Success) {
            "CoinListState must be success"
        }
        val coinState = (coinStateFlow.value as CoinListState.SuccessWithUIProperties)

        if (sortOrder == coinState.sort.sortOrder) return

        coinStateFlow.update {
            coinState.copy(sort = coinState.sort.copy(sortOrder = sortOrder))
        }
        changeSortType(coinState.sort.sortType)
    }

    fun showPopUp() {
        require(coinStateFlow.value is CoinListState.Success) {
            "CoinListState must be success"
        }
        val coinState = (coinStateFlow.value as CoinListState.SuccessWithUIProperties)
        coinStateFlow.update {
            coinState.copy(sortPopVisibility = true)
        }
    }

    fun dismissPopUp() {
        require(coinStateFlow.value is CoinListState.Success) {
            "CoinListState must be success"
        }
        val coinState = (coinStateFlow.value as CoinListState.SuccessWithUIProperties)
        coinStateFlow.update {
            coinState.copy(sortPopVisibility = false)
        }
    }

    fun handleToTop(shouldShow: Boolean) {
        require(coinStateFlow.value is CoinListState.Success) {
            "CoinListState must be success"
        }
        val coinState = (coinStateFlow.value as CoinListState.SuccessWithUIProperties)

        coinStateFlow.update {
            coinState.copy(toTopVisibility = shouldShow)
        }
    }

    fun changeSortType(sortType: SortType) {
        require(coinStateFlow.value is CoinListState.Success) {
            "CoinListState must be success"
        }
        var coinState = (coinStateFlow.value as CoinListState.Success)
        val coins = coinState.coins
        val sortOrder = (coinStateFlow.value as CoinListState.Success).sort.sortOrder
        val newList: List<Coin>
        with(coins) {
            newList = when (sortType) {
                SortType.RANK -> {
                    val selector: (Coin) -> Int? = { coin -> coin.rank }
                    if (sortOrder == SortOrder.ASCENDING) sortedBy(selector)
                    else sortedByDescending(selector)
                }

                SortType.NAME -> {
                    val selector: (Coin) -> String? = { coin -> coin.name }
                    if (sortOrder == SortOrder.ASCENDING) sortedBy(selector)
                    else sortedByDescending(selector)
                }

                SortType.SYMBOL -> {
                    val selector: (Coin) -> String? = { coin -> coin.symbol }
                    if (sortOrder == SortOrder.ASCENDING) sortedBy(selector)
                    else sortedByDescending(selector)
                }
            }
        }

        coinState = (coinStateFlow.value as CoinListState.SuccessWithUIProperties)

        coinStateFlow.update {
            coinState.copy(
                coins = newList, sort = coinState.sort.copy(
                    sortType = sortType
                ), sortPopVisibility = !coinState.sortPopVisibility
            )
        }
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            println("Thread: ${Thread.currentThread().name}")
            coinStateFlow.update { CoinListState.Loading }
            coinRepository.getCoins().flowOn(dispatcherProvider.default).catch {
                coinStateFlow.update { CoinListState.Error("An error occurred") }
            }.collect { coins ->
                println("Thread: ${Thread.currentThread().name}")
                coinStateFlow.update {
                    CoinListState.SuccessWithUIProperties(
                        coins.sortedBy { coin -> coin.rank })
                }
            }
        }
    }
}