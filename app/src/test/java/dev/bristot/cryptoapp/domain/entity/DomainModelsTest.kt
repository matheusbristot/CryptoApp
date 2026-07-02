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

    @Test
    fun marketReview_copyProducesUpdatedValueWithoutChangingOriginal() {
        val original = MarketReview(
            marketCapUsd = 1_000_000L,
            volume24hUsd = 250_000L,
            bitcoinDominancePercentage = 54.3,
            cryptocurrenciesNumber = 10_500,
            marketCapAthValue = 2_000_000L,
            marketCapAthDate = "2026-01-01",
            volume24hAthValue = 500_000L,
            volume24hAthDate = "2026-02-01",
            marketCapChange24h = 1.25,
            volume24hChange24h = -0.75,
            lastUpdated = 1_725_000_000L,
        )

        val updated = original.copy(marketCapUsd = 2_000_000L)

        assertEquals(1_000_000L, original.marketCapUsd)
        assertEquals(2_000_000L, updated.marketCapUsd)
        assertEquals(original.volume24hUsd, updated.volume24hUsd)
    }
}
