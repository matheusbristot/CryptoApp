package dev.bristot.cryptoapp.feature.tickers.data.local

import androidx.room.Entity

@Entity(
    tableName = "ticker_cache",
    primaryKeys = ["coinId", "quotesKey"],
)
data class TickerCacheEntity(
    val coinId: String,
    val quotesKey: String,
    val payload: String,
    val serverUpdatedAt: String,
    val fetchedAtEpochMillis: Long,
)
