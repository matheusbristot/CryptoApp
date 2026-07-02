package dev.bristot.cryptoapp.data.datasource.market_review

import dev.bristot.cryptoapp.data.model.MarketReviewResponse
import kotlinx.coroutines.flow.Flow

interface MarketReviewDataSource {
    fun getMarketOverviewData(): Flow<MarketReviewResponse>
}