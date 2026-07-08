package dev.bristot.cryptoapp.data.dto

import dev.bristot.cryptoapp.data.model.CoinResponse
import dev.bristot.cryptoapp.data.model.TickerResponse
import dev.bristot.cryptoapp.data.model.CurrencyResponse
import dev.bristot.cryptoapp.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.domain.entity.Currency
import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.entity.MarketCap
import dev.bristot.cryptoapp.domain.entity.PercentChangeInterval
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMappersTest {

    @Test
    fun coinDTO_mapsCoinResponseToCoin() {
        val coin = CoinResponse(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            rank = 1,
            isNew = false,
            isActive = true,
            type = "coin",
        ).coinDTO()

        assertEquals("btc", coin.id)
        assertEquals("Bitcoin", coin.name)
        assertEquals("BTC", coin.symbol)
        assertEquals(1, coin.rank)
        assertEquals(true, coin.isActive)
    }

    @Test
    fun toTicker_mapsNestedCurrencyDataToDomain() {
        val ticker = TickerResponse(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            rank = 1,
            totalSupply = 1_000L,
            maxSupply = 2_000L,
            betaValue = 1.0,
            firstDataAt = "2026-07-01T00:00:00Z",
            lastUpdated = "2026-07-01T00:00:00Z",
            quotes = mapOf("USD" to currencyResponse()),
        ).toTicker(currenciesOf = setOf(CurrencySymbol.USD))

        assertEquals("btc", ticker.id)
        assertEquals("Bitcoin", ticker.name)
        assertEquals(
            Currency(
                price = 71_420.0,
                volume24h = 100.0,
                volume24hChange24h = 1.5,
                marketCap = MarketCap(
                    marketCap = 1_000.0,
                    lastChangeTwentyFourHours = 2.5,
                ),
                percentChangeInterval = PercentChangeInterval(
                    p15m = 0.1,
                    p30m = 0.2,
                    p1h = 0.3,
                    p6h = 0.4,
                    p12h = 0.5,
                    p24h = 0.6,
                    p7d = 0.7,
                    p30d = 0.8,
                    p1y = 0.9,
                ),
                allTimeHigh = AllTimeHigh(
                    price = 3_000.0,
                    date = "2026-01-01T00:00:00Z",
                    percentage = -10.0,
                ),
            ),
            ticker.prices.getValue(CurrencySymbol.USD),
        )
    }

    private fun currencyResponse() = CurrencyResponse(
        price = 71_420.0,
        volume24h = 100.0,
        volume24hChange24h = 1.5,
        marketCap = 1_000.0,
        marketCapChange24h = 2.5,
        percentChange15m = 0.1,
        percentChange30m = 0.2,
        percentChange1h = 0.3,
        percentChange6h = 0.4,
        percentChange12h = 0.5,
        percentChange24h = 0.6,
        percentChange7d = 0.7,
        percentChange30d = 0.8,
        percentChange1y = 0.9,
        athPrice = 3_000.0,
        athDate = "2026-01-01T00:00:00Z",
        percentFromPriceAth = -10.0,
    )
}
