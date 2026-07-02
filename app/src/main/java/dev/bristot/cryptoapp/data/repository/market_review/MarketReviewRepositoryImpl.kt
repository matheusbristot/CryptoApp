package dev.bristot.cryptoapp.data.repository.market_review

import dev.bristot.cryptoapp.data.datasource.market_review.MarketReviewDataSource
import dev.bristot.cryptoapp.data.dto.toMarketReview
import dev.bristot.cryptoapp.domain.entity.MarketReview
import dev.bristot.cryptoapp.domain.repository.MarketReviewRepository
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