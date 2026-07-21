package dev.bristot.cryptoapp.feature.favorites.data

import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.SyncScheduler
import dev.bristot.cryptoapp.time.TimeProvider
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomFavoritesRepository @Inject constructor(
    private val dao: FavoritesDao,
    private val syncScheduler: SyncScheduler,
    private val timeProvider: TimeProvider,
    private val logger: CryptoLogger,
) : FavoritesRepository {

    override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> =
        dao.observeFavorites(type.name).map { entities ->
            entities.map { entity -> entity.toFavoriteRef() }
        }

    override fun observeIsFavorite(
        type: FavoriteType,
        itemId: String,
    ): Flow<Boolean> = dao.observeIsFavorite(type.name, itemId)

    override suspend fun setFavorite(
        type: FavoriteType,
        itemId: String,
        isFavorite: Boolean,
    ) {
        val normalizedId = itemId.trim()
        require(normalizedId.isNotEmpty()) { "Favorite item ID must not be blank" }

        val changed = if (isFavorite) {
            dao.insert(
                FavoriteEntity(
                    type = type.name,
                    itemId = normalizedId,
                    createdAtEpochMillis = timeProvider.currentTimeMillis(),
                )
            ) != INSERT_IGNORED
        } else {
            dao.delete(type.name, normalizedId) > 0
        }

        if (changed) reconcileSync()
    }

    private suspend fun reconcileSync() {
        try {
            syncScheduler.scheduleAll()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            logger.warning(
                message = "Favorites changed, but sync reconciliation failed",
                throwable = exception,
            )
        }
    }

    private fun FavoriteEntity.toFavoriteRef(): FavoriteRef = FavoriteRef(
        type = FavoriteType.valueOf(type),
        itemId = itemId,
        createdAtEpochMillis = createdAtEpochMillis,
    )

    private companion object {
        const val INSERT_IGNORED = -1L
    }
}
