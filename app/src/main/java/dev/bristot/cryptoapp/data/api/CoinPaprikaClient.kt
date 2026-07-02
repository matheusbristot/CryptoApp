package dev.bristot.cryptoapp.data.api

import retrofit2.Retrofit

interface CoinPaprikaClient {
    fun client(): Retrofit
}
