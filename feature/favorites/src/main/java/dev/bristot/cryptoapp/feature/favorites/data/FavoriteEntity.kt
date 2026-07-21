package dev.bristot.cryptoapp.feature.favorites.data

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["type", "itemId"],
)
internal data class FavoriteEntity(
    val type: String,
    val itemId: String,
    val createdAtEpochMillis: Long,
)
