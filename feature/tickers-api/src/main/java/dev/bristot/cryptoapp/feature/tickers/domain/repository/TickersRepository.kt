package dev.bristot.cryptoapp.feature.tickers.domain.repository

import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import kotlinx.coroutines.flow.Flow

interface TickersRepository {
    suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>>
    suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker>
    fun observeTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker?>
    suspend fun refreshTicker(
        coinId: String,
        currencies: Set<CurrencySymbol>,
        force: Boolean = false,
    )
}
