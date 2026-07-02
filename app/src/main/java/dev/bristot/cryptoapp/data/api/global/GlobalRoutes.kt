package dev.bristot.cryptoapp.data.api.global

import dev.bristot.cryptoapp.data.model.MarketReviewResponse
import retrofit2.http.GET

private const val baseGlobalPath: String = "global"


interface GlobalRoutes {
    @GET(baseGlobalPath)
    suspend fun getMarketReviewData(): MarketReviewResponse
}
