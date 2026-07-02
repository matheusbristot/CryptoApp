package dev.bristot.cryptoapp.domain.repository

import dev.bristot.cryptoapp.domain.entity.MarketReview
import kotlinx.coroutines.flow.Flow

interface MarketReviewRepository {
    suspend fun getMarketReviewData(): Flow<MarketReview>
}
