package dev.bristot.cryptoapp.feature.tickers.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomTickersLocalDataSourceTest {

    private lateinit var database: TickersCacheDatabase
    private lateinit var localDataSource: RoomTickersLocalDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TickersCacheDatabase::class.java,
        ).allowMainThreadQueries().build()
        localDataSource = RoomTickersLocalDataSource(
            database = database,
            json = Json { encodeDefaults = true },
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertTicker_doesNotReplaceNewerServerData() = runBlocking {
        localDataSource.upsertTicker(
            response = ticker(lastUpdated = "2026-07-18T10:00:00Z", rank = 1),
            quotesKey = "BRL",
            fetchedAtEpochMillis = 2L,
        )
        localDataSource.upsertTicker(
            response = ticker(lastUpdated = "2026-07-18T09:59:00Z", rank = 2),
            quotesKey = "BRL",
            fetchedAtEpochMillis = 3L,
        )

        val cached = localDataSource.getTicker("btc-bitcoin", "BRL")

        assertEquals(1, cached?.response?.rank)
        assertEquals(2L, cached?.fetchedAtEpochMillis)
    }

    @Test
    fun getTicker_deletesPayloadThatCannotBeDeserialized() = runBlocking {
        database.tickerCacheDao().upsert(
            TickerCacheEntity(
                coinId = "btc-bitcoin",
                quotesKey = "BRL",
                payload = "{\"obsolete\":true}",
                serverUpdatedAt = "2026-07-18T10:00:00Z",
                fetchedAtEpochMillis = 2L,
            )
        )

        val cached = localDataSource.getTicker("btc-bitcoin", "BRL")

        assertNull(cached)
        assertNull(database.tickerCacheDao().get("btc-bitcoin", "BRL"))
    }

    private fun ticker(lastUpdated: String, rank: Int) = TickerResponse(
        id = "btc-bitcoin",
        name = "Bitcoin",
        symbol = "BTC",
        rank = rank,
        totalSupply = 1L,
        maxSupply = 2L,
        betaValue = 1.0,
        firstDataAt = "2010-07-17T00:00:00Z",
        lastUpdated = lastUpdated,
        quotes = emptyMap(),
    )
}
