package dev.bristot.cryptoapp.domain.repository

import dev.bristot.cryptoapp.domain.entity.Ticker
import kotlinx.coroutines.flow.StateFlow

interface RecentTickersRepository {
    fun observeRecentTickers(): StateFlow<List<Ticker>>
    fun addRecentTicker(ticker: Ticker)
}
