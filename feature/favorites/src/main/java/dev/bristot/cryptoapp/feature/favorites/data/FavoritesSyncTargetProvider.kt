package dev.bristot.cryptoapp.feature.favorites.data

import dev.bristot.cryptoapp.sync.api.SyncTarget
import dev.bristot.cryptoapp.sync.api.SyncTargetProvider
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import javax.inject.Inject

internal class FavoritesSyncTargetProvider @Inject constructor(
    private val dao: FavoritesDao,
) : SyncTargetProvider {

    override suspend fun targets(): Set<SyncTarget> = dao.getAll()
        .mapTo(mutableSetOf()) { entity ->
            SyncTarget(
                type = SyncTargetType.valueOf(entity.type),
                id = entity.itemId,
            )
        }
}
