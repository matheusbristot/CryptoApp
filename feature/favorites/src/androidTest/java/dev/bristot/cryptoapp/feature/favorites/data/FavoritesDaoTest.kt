package dev.bristot.cryptoapp.feature.favorites.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    private lateinit var database: FavoritesDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavoritesDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun sameId_isStoredIndependentlyForCoinsAndTickers() = runBlocking {
        val dao = database.favoritesDao()
        dao.insert(FavoriteEntity(FavoriteType.COIN.name, "btc-bitcoin", 1L))
        dao.insert(FavoriteEntity(FavoriteType.TICKER.name, "btc-bitcoin", 2L))

        assertEquals(1, dao.observeFavorites(FavoriteType.COIN.name).first().size)
        assertEquals(1, dao.observeFavorites(FavoriteType.TICKER.name).first().size)

        dao.delete(FavoriteType.COIN.name, "btc-bitcoin")

        assertEquals(0, dao.observeFavorites(FavoriteType.COIN.name).first().size)
        assertEquals(1, dao.observeFavorites(FavoriteType.TICKER.name).first().size)
    }
}
