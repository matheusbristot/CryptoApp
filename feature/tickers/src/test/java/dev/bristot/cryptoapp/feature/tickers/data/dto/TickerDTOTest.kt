package dev.bristot.cryptoapp.feature.tickers.data.dto

import dev.bristot.cryptoapp.feature.tickers.data.model.CurrencyResponse
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import org.junit.Assert.assertEquals
import org.junit.Test

class TickerDTOTest {

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
        assertEquals(1_000L, ticker.totalSupply)
        assertEquals(2_000L, ticker.maxSupply)
        assertEquals(1.0, ticker.betaValue, 0.0)
        assertEquals("2026-07-01T00:00:00Z", ticker.lastUpdated)
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

    @Test
    fun toTicker_mapsMultipleRequestedCurrenciesToDomain() {
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
            quotes = mapOf(
                "USD" to currencyResponse(price = 71_420.0),
                "BRL" to currencyResponse(price = 392_810.0),
            ),
        ).toTicker(currenciesOf = setOf(CurrencySymbol.USD, CurrencySymbol.BRL))

        assertEquals(71_420.0, ticker.prices.getValue(CurrencySymbol.USD).price, 0.0)
        assertEquals(392_810.0, ticker.prices.getValue(CurrencySymbol.BRL).price, 0.0)
    }

    private fun currencyResponse(price: Double = 71_420.0) = CurrencyResponse(
        price = price,
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
