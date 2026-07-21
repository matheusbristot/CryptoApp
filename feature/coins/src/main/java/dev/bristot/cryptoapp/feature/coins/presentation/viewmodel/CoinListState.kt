package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin

enum class CoinListSection {
    ALL,
    FAVORITES,
}

data class FavoriteCoinItem(
    val id: String,
    val coin: Coin?,
)

sealed class CoinListState {
    object Initial : CoinListState()
    object Loading : CoinListState()
    open class Success(
        open val coins: List<Coin>,
    ) : CoinListState()

    data class SuccessWithUIProperties(
        override val coins: List<Coin>,
        val toTopVisibility: Boolean = false,
    ) : Success(coins = coins)

    data class Error(val message: String) : CoinListState()
}
