package dev.bristot.cryptoapp.domain.entity

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainModelsTest {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Test
    fun ticker_serializesAndDeserializesNestedValues() {
        val ticker = Ticker(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            rank = 1,
            prices = mapOf(
                CurrencySymbol.BRL to Currency(
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
                )
            )
        )

        val serialized = json.encodeToString(Ticker.serializer(), ticker)
        val decoded = json.decodeFromString(Ticker.serializer(), serialized)

        assertEquals(ticker, decoded)
    }

}
