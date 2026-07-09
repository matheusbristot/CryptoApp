package dev.bristot.cryptoapp.feature.coins.data.datasource.coins

import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import kotlinx.coroutines.flow.Flow

interface CoinsDatasource {
    suspend fun getCoins(): Flow<List<CoinResponse>>
}