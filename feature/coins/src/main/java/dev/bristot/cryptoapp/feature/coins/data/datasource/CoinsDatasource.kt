package dev.bristot.cryptoapp.feature.coins.data.datasource

import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import kotlinx.coroutines.flow.Flow

interface CoinsDatasource {
    suspend fun getCoins(): Flow<List<CoinResponse>>
    suspend fun getCoin(coinId: String): CoinResponse
}
