package dev.bristot.cryptoapp.feature.market_review.data.dto

import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import dev.bristot.cryptoapp.feature.market_review.domain.entity.MarketReview
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketReviewDTOTest {

    @Test
    fun toMarketReview_mapsMarketReviewResponseToDomain() {
        val marketReview = MarketReviewResponse(
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
        ).toMarketReview()

        assertEquals(
            MarketReview(
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
            ),
            marketReview,
        )
    }
}
