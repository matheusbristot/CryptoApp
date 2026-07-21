package dev.bristot.cryptoapp.feature.favorites.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}
