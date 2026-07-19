package dev.bristot.cryptoapp.feature.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_cache")
data class CoinCacheEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val isNew: Boolean,
    val isActive: Boolean,
    val type: String,
    val fetchedAtEpochMillis: Long,
)
