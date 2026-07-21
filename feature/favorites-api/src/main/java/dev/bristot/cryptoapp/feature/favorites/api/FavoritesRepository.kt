package dev.bristot.cryptoapp.feature.favorites.api

import kotlinx.coroutines.flow.Flow

enum class FavoriteType {
    COIN,
    TICKER,
}

data class FavoriteRef(
    val type: FavoriteType,
    val itemId: String,
    val createdAtEpochMillis: Long,
)

interface FavoritesRepository {
    fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>>

    fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean>

    suspend fun setFavorite(type: FavoriteType, itemId: String, isFavorite: Boolean)
}
