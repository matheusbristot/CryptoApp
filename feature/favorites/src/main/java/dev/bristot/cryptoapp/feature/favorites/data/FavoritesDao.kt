package dev.bristot.cryptoapp.feature.favorites.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoritesDao {

    @Query("SELECT * FROM favorites WHERE type = :type ORDER BY createdAtEpochMillis DESC, itemId ASC")
    fun observeFavorites(type: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND itemId = :itemId)")
    fun observeIsFavorite(type: String, itemId: String): Flow<Boolean>

    @Query("SELECT * FROM favorites ORDER BY type ASC, createdAtEpochMillis DESC, itemId ASC")
    suspend fun getAll(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE type = :type AND itemId = :itemId")
    suspend fun delete(type: String, itemId: String): Int
}
