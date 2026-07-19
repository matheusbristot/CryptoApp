package dev.bristot.cryptoapp.feature.tickers.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TickerCacheDao {

    @Query("SELECT * FROM ticker_cache WHERE coinId = :coinId AND quotesKey = :quotesKey")
    fun observe(coinId: String, quotesKey: String): Flow<TickerCacheEntity?>

    @Query("SELECT * FROM ticker_cache WHERE coinId = :coinId AND quotesKey = :quotesKey")
    suspend fun get(coinId: String, quotesKey: String): TickerCacheEntity?

    @Query("DELETE FROM ticker_cache WHERE coinId = :coinId AND quotesKey = :quotesKey")
    suspend fun delete(coinId: String, quotesKey: String)

    @Upsert
    suspend fun upsert(entity: TickerCacheEntity)
}
