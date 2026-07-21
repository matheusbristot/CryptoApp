package dev.bristot.cryptoapp.feature.favorites.data

import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.SyncScheduler
import dev.bristot.cryptoapp.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomFavoritesRepositoryTest {

    @Test
    fun `favorites are independent by type and reconcile sync after changes`() = runTest {
        val dao = FakeFavoritesDao()
        val scheduler = FakeSyncScheduler()
        val repository = RoomFavoritesRepository(
            dao = dao,
            syncScheduler = scheduler,
            timeProvider = TimeProvider { 42L },
            logger = NoOpLogger,
        )

        repository.setFavorite(FavoriteType.COIN, "btc-bitcoin", true)
        repository.setFavorite(FavoriteType.TICKER, "btc-bitcoin", true)

        assertEquals("btc-bitcoin", repository.observeFavorites(FavoriteType.COIN).first().single().itemId)
        assertEquals("btc-bitcoin", repository.observeFavorites(FavoriteType.TICKER).first().single().itemId)
        assertEquals(2, scheduler.scheduleCalls)

        repository.setFavorite(FavoriteType.COIN, "btc-bitcoin", true)
        assertEquals(2, scheduler.scheduleCalls)

        repository.setFavorite(FavoriteType.COIN, "btc-bitcoin", false)

        assertFalse(repository.observeIsFavorite(FavoriteType.COIN, "btc-bitcoin").first())
        assertTrue(repository.observeIsFavorite(FavoriteType.TICKER, "btc-bitcoin").first())
        assertEquals(3, scheduler.scheduleCalls)
    }

    @Test
    fun `sync provider preserves target type`() = runTest {
        val dao = FakeFavoritesDao()
        dao.insert(FavoriteEntity(FavoriteType.COIN.name, "btc-bitcoin", 2L))
        dao.insert(FavoriteEntity(FavoriteType.TICKER.name, "eth-ethereum", 1L))

        val targets = FavoritesSyncTargetProvider(dao).targets()

        assertEquals(setOf("COIN:btc-bitcoin", "TICKER:eth-ethereum"), targets.map { "${it.type}:${it.id}" }.toSet())
    }

    private class FakeSyncScheduler : SyncScheduler {
        var scheduleCalls = 0

        override suspend fun scheduleAll() {
            scheduleCalls += 1
        }

        override fun cancel(taskKey: String) = Unit
    }

    private class FakeFavoritesDao : FavoritesDao {
        private val state = MutableStateFlow<List<FavoriteEntity>>(emptyList())

        override fun observeFavorites(type: String): Flow<List<FavoriteEntity>> = state.map { entities ->
            entities.filter { entity -> entity.type == type }
                .sortedWith(compareByDescending<FavoriteEntity> { it.createdAtEpochMillis }.thenBy { it.itemId })
        }

        override fun observeIsFavorite(type: String, itemId: String): Flow<Boolean> = state.map { entities ->
            entities.any { entity -> entity.type == type && entity.itemId == itemId }
        }

        override suspend fun getAll(): List<FavoriteEntity> = state.value

        override suspend fun insert(entity: FavoriteEntity): Long {
            if (state.value.any { current -> current.type == entity.type && current.itemId == entity.itemId }) {
                return -1L
            }
            state.value += entity
            return state.value.lastIndex.toLong()
        }

        override suspend fun delete(type: String, itemId: String): Int {
            val updated = state.value.filterNot { entity -> entity.type == type && entity.itemId == itemId }
            if (updated.size == state.value.size) return 0
            state.value = updated
            return 1
        }
    }

    private object NoOpLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}
