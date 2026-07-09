package dev.bristot.cryptoapp.feature.coins.data.api.coins

import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import retrofit2.http.GET
import retrofit2.http.Path


private const val baseCoinsPath: String = "coins"

interface CoinsRoutes {

    @GET(baseCoinsPath)
    suspend fun getCoins(): List<CoinResponse>

    @GET("$baseCoinsPath/{coinId}")
    suspend fun getCoinById(@Path("coinId") coinId: String): CoinResponse
}
