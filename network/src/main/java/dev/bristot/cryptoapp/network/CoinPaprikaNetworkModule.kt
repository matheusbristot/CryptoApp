package dev.bristot.cryptoapp.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoinPaprikaNetworkModule {

    @Provides
    @Singleton
    fun provideCoinPaprikaRouteFactory(): CoinPaprikaRouteFactory = RetrofitCoinPaprikaRouteFactory(
        retrofit = CoinPaprikaClientAPI(
            json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                coerceInputValues = true
                allowSpecialFloatingPointValues = true
            },
            versionAPI = "v1/",
            contentMediaType = "application/json".toMediaType()
        ).client()
    )
}
