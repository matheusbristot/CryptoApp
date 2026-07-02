package dev.bristot.cryptoapp.data.datasource.tickers

import dev.bristot.cryptoapp.data.model.TickerResponse
import kotlinx.coroutines.flow.Flow

interface TickersDataSource {
    suspend fun getTickers(currencies: List<String>): Flow<List<TickerResponse>>
    suspend fun getTicker(coinId: String, currencies: List<String>): Flow<TickerResponse>
}
