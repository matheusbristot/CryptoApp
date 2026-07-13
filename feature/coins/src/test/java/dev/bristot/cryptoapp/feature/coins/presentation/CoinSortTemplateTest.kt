package dev.bristot.cryptoapp.feature.coins.presentation

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinSortTemplateTest {

    private val template = CoinSortTemplate()
    private val coins = listOf(
        coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2),
        coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1),
    )

    @Test
    fun sort_mapsCoinRankToTemplate() {
        assertEquals(
            listOf("btc", "eth"),
            template.sort(coins, SortState(type = SortType.RANK)).map(Coin::id),
        )
    }

    @Test
    fun sort_mapsCoinNameToTemplate() {
        assertEquals(
            listOf("eth", "btc"),
            template.sort(
                coins,
                SortState(type = SortType.NAME, order = SortOrder.DESCENDING),
            ).map(Coin::id),
        )
    }

    @Test
    fun sort_mapsCoinSymbolToTemplate() {
        assertEquals(
            listOf("btc", "eth"),
            template.sort(coins, SortState(type = SortType.SYMBOL)).map(Coin::id),
        )
    }

    private fun coin(id: String, name: String, symbol: String, rank: Int) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        isNew = false,
        isActive = true,
        type = "coin",
    )
}
