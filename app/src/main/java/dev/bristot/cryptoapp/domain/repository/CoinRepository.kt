package dev.bristot.cryptoapp.domain.repository

import dev.bristot.cryptoapp.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    suspend fun getCoins(): Flow<List<Coin>>
}