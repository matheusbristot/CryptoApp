package dev.bristot.cryptoapp.presentation.tickers

import dev.bristot.cryptoapp.domain.entity.Currency
import dev.bristot.cryptoapp.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.entity.MarketCap
import dev.bristot.cryptoapp.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Test

class TickerSortTest {

    @Test
    fun sortTickers_byRankAscending_ordersByRank() {
        val tickers = listOf(
            ticker(id = "b", name = "Beta", symbol = "B", rank = 2),
            ticker(id = "a", name = "Alpha", symbol = "A", rank = 1),
        )

        val sorted = sortTickers(
            tickers = tickers,
            sortType = SortType.RANK,
            sortOrder = SortOrder.ASCENDING,
        )

        assertEquals(listOf("a", "b"), sorted.map { it.id })
    }

    @Test
    fun sortTickers_byNameDescending_ordersByName() {
        val tickers = listOf(
            ticker(id = "b", name = "Beta", symbol = "B", rank = 2),
            ticker(id = "a", name = "Alpha", symbol = "A", rank = 1),
        )

        val sorted = sortTickers(
            tickers = tickers,
            sortType = SortType.NAME,
            sortOrder = SortOrder.DESCENDING,
        )

        assertEquals(listOf("b", "a"), sorted.map { it.id })
    }

    private fun ticker(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
    ) = Ticker(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        prices = mapOf(
            CurrencySymbol.BRL to Currency(
                price = 1.0,
                volume24h = 1.0,
                volume24hChange24h = 1.0,
                marketCap = MarketCap(marketCap = 1.0, lastChangeTwentyFourHours = 1.0),
                percentChangeInterval = PercentChangeInterval(
                    p15m = 1.0,
                    p30m = 1.0,
                    p1h = 1.0,
                    p6h = 1.0,
                    p12h = 1.0,
                    p24h = 1.0,
                    p7d = 1.0,
                    p30d = 1.0,
                    p1y = 1.0,
                ),
                allTimeHigh = AllTimeHigh(price = 1.0, date = "2026-07-01", percentage = 1.0),
            ),
        ),
    )
}
