package dev.bristot.cryptoapp.feature.coins.domain.repository

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    suspend fun getCoins(): Flow<List<Coin>>
}