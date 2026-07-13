package dev.bristot.cryptoapp.feature.coins.presentation

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import javax.inject.Inject

class CoinSortTemplate @Inject constructor() : SortTemplate<Coin>() {
    override fun rankOf(item: Coin): Int = item.rank
    override fun nameOf(item: Coin): String = item.name
    override fun symbolOf(item: Coin): String = item.symbol
}
