package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.CoinListSort
import dev.bristot.cryptoapp.feature.coins.presentation.SortType

sealed class CoinListState {
    object Initial : CoinListState()
    object Loading : CoinListState()
    open class Success(
        open val coins: List<Coin>,
        open val sort: CoinListSort,
    ) : CoinListState()

    data class SuccessWithUIProperties(
        override val coins: List<Coin>,
        override val sort: CoinListSort = CoinListSort(
            sortType = SortType.RANK
        ),
        val toTopVisibility: Boolean = false,
        val sortPopVisibility: Boolean = false,
    ) : Success(coins = coins, sort = sort)

    data class Error(val message: String) : CoinListState()
}