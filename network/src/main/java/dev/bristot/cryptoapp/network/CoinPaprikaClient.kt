package dev.bristot.cryptoapp.network

import retrofit2.Retrofit

internal interface CoinPaprikaClient {
    fun client(): Retrofit
}
