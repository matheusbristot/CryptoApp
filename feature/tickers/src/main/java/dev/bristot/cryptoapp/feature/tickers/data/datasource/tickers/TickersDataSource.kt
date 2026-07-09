package dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers

import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import kotlinx.coroutines.flow.Flow

interface TickersDataSource {
    suspend fun getTickers(currencies: List<String>): Flow<List<TickerResponse>>
    suspend fun getTicker(coinId: String, currencies: List<String>): Flow<TickerResponse>
}
