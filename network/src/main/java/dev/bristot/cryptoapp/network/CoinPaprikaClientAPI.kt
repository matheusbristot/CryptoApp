package dev.bristot.cryptoapp.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

internal class CoinPaprikaClientAPI(
    private val json: Json,
    private val versionAPI: String,
    private val contentMediaType: MediaType,
) : CoinPaprikaClient {
    override fun client(): Retrofit {
        val converter = json.asConverterFactory(contentMediaType)
        return Retrofit.Builder()
            .baseUrl("https://api.coinpaprika.com/$versionAPI")
            .addConverterFactory(converter)
            .build()
    }
}
