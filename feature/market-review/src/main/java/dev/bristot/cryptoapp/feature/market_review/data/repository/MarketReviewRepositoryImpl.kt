package dev.bristot.cryptoapp.feature.market_review.data.repository

import dev.bristot.cryptoapp.feature.market_review.data.datasource.MarketReviewDataSource
import dev.bristot.cryptoapp.feature.market_review.data.dto.toMarketReview
import dev.bristot.cryptoapp.feature.market_review.domain.entity.MarketReview
import dev.bristot.cryptoapp.feature.market_review.domain.repository.MarketReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MarketReviewRepositoryImpl @Inject constructor(
    private val marketReviewDataSource: MarketReviewDataSource
) : MarketReviewRepository {
    override suspend fun getMarketReviewData(): Flow<MarketReview> {
        return marketReviewDataSource.getMarketOverviewData().map { marketReviewResponse ->
            marketReviewResponse.toMarketReview()
        }
    }
}
