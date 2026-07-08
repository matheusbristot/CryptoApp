package dev.bristot.cryptoapp.feature.market_review.data.api.global

import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import retrofit2.http.GET

private const val baseGlobalPath: String = "global"


interface GlobalRoutes {
    @GET(baseGlobalPath)
    suspend fun getMarketReviewData(): MarketReviewResponse
}
