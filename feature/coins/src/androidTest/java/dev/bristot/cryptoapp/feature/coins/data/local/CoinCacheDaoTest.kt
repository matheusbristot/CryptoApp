package dev.bristot.cryptoapp.feature.coins.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoinCacheDaoTest {

    private lateinit var database: CoinsCacheDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CoinsCacheDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsert_replacesValueAndNotifiesObserver() = runBlocking {
        val dao = database.coinCacheDao()
        dao.upsert(entity(name = "Old Bitcoin", fetchedAt = 1L))
        dao.upsert(entity(name = "Bitcoin", fetchedAt = 2L))

        val cached = dao.observe("btc-bitcoin").first()

        assertEquals("Bitcoin", cached?.name)
        assertEquals(2L, cached?.fetchedAtEpochMillis)
    }

    private fun entity(name: String, fetchedAt: Long) = CoinCacheEntity(
        id = "btc-bitcoin",
        name = name,
        symbol = "BTC",
        rank = 1,
        isNew = false,
        isActive = true,
        type = "coin",
        fetchedAtEpochMillis = fetchedAt,
    )
}
