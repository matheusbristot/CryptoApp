package dev.bristot.cryptoapp.feature.market_review.data.repository

import dev.bristot.cryptoapp.feature.market_review.data.datasource.MarketReviewDataSource
import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import dev.bristot.cryptoapp.feature.market_review.domain.entity.MarketReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketReviewRepositoryImplTest {

    @Test
    fun getMarketReviewData_mapsResponseToDomain() = runBlocking {
        val dataSource = FakeMarketReviewDataSource(
            response = MarketReviewResponse(
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
        )
        val repository = MarketReviewRepositoryImpl(marketReviewDataSource = dataSource)

        val marketReview = repository.getMarketReviewData().first()

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
            marketReview
        )
    }

    private class FakeMarketReviewDataSource(
        private val response: MarketReviewResponse,
    ) : MarketReviewDataSource {
        override fun getMarketOverviewData(): Flow<MarketReviewResponse> = flowOf(response)
    }
}
