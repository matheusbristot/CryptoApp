package dev.bristot.cryptoapp.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.data.api.tickers.TickersRoutes
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object APIModule {


    @Provides
    @Singleton
    fun provideSerialization(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
        allowSpecialFloatingPointValues = true
    }

    @Provides
    @Singleton
    fun providesMediaType(): MediaType = "application/json".toMediaType()

    @Provides
    @Singleton
    fun provideRetrofit(json: Json, contentMediaType: MediaType): Retrofit = CoinPaprikaClientAPI(
        json = json, versionAPI = "v1/", contentMediaType = contentMediaType
    ).client()

    @Provides
    @Singleton
    fun provideTickersRoute(client: Retrofit): TickersRoutes = client.create<TickersRoutes>()
}
