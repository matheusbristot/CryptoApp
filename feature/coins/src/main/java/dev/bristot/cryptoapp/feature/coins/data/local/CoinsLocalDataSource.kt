package dev.bristot.cryptoapp.feature.coins.data.local

import kotlinx.coroutines.flow.Flow

interface CoinsLocalDataSource {
    fun observeCoin(coinId: String): Flow<CoinCacheEntity?>
    suspend fun getCoin(coinId: String): CoinCacheEntity?
    suspend fun upsertCoin(entity: CoinCacheEntity)
}

class RoomCoinsLocalDataSource(
    private val dao: CoinCacheDao,
) : CoinsLocalDataSource {
    override fun observeCoin(coinId: String): Flow<CoinCacheEntity?> = dao.observe(coinId)

    override suspend fun getCoin(coinId: String): CoinCacheEntity? = dao.get(coinId)

    override suspend fun upsertCoin(entity: CoinCacheEntity) = dao.upsert(entity)
}
