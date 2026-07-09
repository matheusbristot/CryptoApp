package dev.bristot.cryptoapp.network

import retrofit2.Retrofit

internal class RetrofitCoinPaprikaRouteFactory(
    private val retrofit: Retrofit,
) : CoinPaprikaRouteFactory {
    override fun <T : Any> create(routeClass: Class<T>): T = retrofit.create(routeClass)
}
