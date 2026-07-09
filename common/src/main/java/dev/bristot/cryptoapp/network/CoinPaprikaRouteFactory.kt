package dev.bristot.cryptoapp.network

interface CoinPaprikaRouteFactory {
    fun <T : Any> create(routeClass: Class<T>): T
}

inline fun <reified T : Any> CoinPaprikaRouteFactory.create(): T = create(T::class.java)
