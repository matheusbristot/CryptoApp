package dev.bristot.cryptoapp.feature.coins.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CoinCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CoinsCacheDatabase : RoomDatabase() {
    abstract fun coinCacheDao(): CoinCacheDao
}
