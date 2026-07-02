package dev.bristot.cryptoapp.data.api.coins

import dev.bristot.cryptoapp.data.model.CoinResponse
import retrofit2.http.GET
import retrofit2.http.Path


private const val baseCoinsPath: String = "coins"

interface CoinsRoutes {

    @GET(baseCoinsPath)
    suspend fun getCoins(): List<CoinResponse>

    @GET("$baseCoinsPath/{coinId}")
    suspend fun getCoinById(@Path("coinId") coinId: String): CoinResponse
}
