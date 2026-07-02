package dev.bristot.cryptoapp.data.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject


class CoinPaprikaClientAPI @Inject constructor(
    private val json: Json,
    private val versionAPI: String,
    private val contentMediaType: MediaType,
) : CoinPaprikaClient {
    override fun client(): Retrofit {
        val converter = json.asConverterFactory(contentMediaType)
        return Retrofit.Builder().baseUrl("https://api.coinpaprika.com/$versionAPI")
            .addConverterFactory(converter).build()
    }
}
