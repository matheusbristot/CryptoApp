package dev.bristot.cryptoapp.feature.tickers.domain.repository

import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import kotlinx.coroutines.flow.StateFlow

interface RecentTickersRepository {
    fun observeRecentTickers(): StateFlow<List<Ticker>>
    fun addRecentTicker(ticker: Ticker)
}
