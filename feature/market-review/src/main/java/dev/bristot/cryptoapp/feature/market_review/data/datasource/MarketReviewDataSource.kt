package dev.bristot.cryptoapp.feature.market_review.data.datasource

import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import kotlinx.coroutines.flow.Flow

interface MarketReviewDataSource {
    fun getMarketOverviewData(): Flow<MarketReviewResponse>
}
