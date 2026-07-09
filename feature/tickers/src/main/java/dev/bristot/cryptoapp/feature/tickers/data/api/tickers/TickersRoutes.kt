package dev.bristot.cryptoapp.feature.tickers.data.api.tickers

import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val baseTickersPath: String = "tickers"

interface TickersRoutes {
    @GET(value = baseTickersPath)
    suspend fun getTickersByQuotes(@Query(value = "quotes") quotes: List<String>): List<TickerResponse>

    @GET(value = "$baseTickersPath/{id}")
    suspend fun getTickerByQuotes(
        @Path(value = "id") coinId: String, @Query(value = "quotes") quotes: List<String>
    ): TickerResponse
}
