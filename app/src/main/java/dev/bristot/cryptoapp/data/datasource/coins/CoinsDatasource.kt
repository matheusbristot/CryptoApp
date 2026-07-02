package dev.bristot.cryptoapp.data.datasource.coins

import dev.bristot.cryptoapp.data.model.CoinResponse
import kotlinx.coroutines.flow.Flow

interface CoinsDatasource {
    suspend fun getCoins(): Flow<List<CoinResponse>>
}