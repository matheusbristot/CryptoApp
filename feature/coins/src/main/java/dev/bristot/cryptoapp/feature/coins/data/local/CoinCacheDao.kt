package dev.bristot.cryptoapp.feature.coins.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinCacheDao {

    @Query("SELECT * FROM coin_cache WHERE id = :coinId")
    fun observe(coinId: String): Flow<CoinCacheEntity?>

    @Query("SELECT * FROM coin_cache WHERE id = :coinId")
    suspend fun get(coinId: String): CoinCacheEntity?

    @Upsert
    suspend fun upsert(entity: CoinCacheEntity)
}
