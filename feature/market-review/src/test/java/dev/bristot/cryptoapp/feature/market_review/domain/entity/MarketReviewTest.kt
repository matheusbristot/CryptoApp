package dev.bristot.cryptoapp.feature.market_review.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class MarketReviewTest {

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
