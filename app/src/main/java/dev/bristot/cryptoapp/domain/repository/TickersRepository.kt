package dev.bristot.cryptoapp.domain.repository

import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.entity.Ticker
import kotlinx.coroutines.flow.Flow

interface TickersRepository {
    suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>>
    suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker>
}
