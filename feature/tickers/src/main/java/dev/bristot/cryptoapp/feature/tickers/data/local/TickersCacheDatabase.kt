package dev.bristot.cryptoapp.feature.tickers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TickerCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TickersCacheDatabase : RoomDatabase() {
    abstract fun tickerCacheDao(): TickerCacheDao
}
