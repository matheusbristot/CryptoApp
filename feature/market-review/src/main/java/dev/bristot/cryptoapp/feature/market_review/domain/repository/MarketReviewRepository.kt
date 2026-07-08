package dev.bristot.cryptoapp.feature.market_review.domain.repository

import dev.bristot.cryptoapp.feature.market_review.domain.entity.MarketReview
import kotlinx.coroutines.flow.Flow

interface MarketReviewRepository {
    suspend fun getMarketReviewData(): Flow<MarketReview>
}
