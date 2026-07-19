package dev.bristot.cryptoapp.feature.tickers.data.local

import androidx.room.withTransaction
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

data class CachedTicker(
    val response: TickerResponse,
    val fetchedAtEpochMillis: Long,
)

interface TickersLocalDataSource {
    fun observeTicker(coinId: String, quotesKey: String): Flow<CachedTicker?>
    suspend fun getTicker(coinId: String, quotesKey: String): CachedTicker?
    suspend fun upsertTicker(
        response: TickerResponse,
        quotesKey: String,
        fetchedAtEpochMillis: Long,
    )
}

class RoomTickersLocalDataSource(
    private val database: TickersCacheDatabase,
    private val json: Json,
) : TickersLocalDataSource {

    private val dao = database.tickerCacheDao()

    override fun observeTicker(coinId: String, quotesKey: String): Flow<CachedTicker?> =
        dao.observe(coinId, quotesKey).map { entity -> entity?.toCachedTickerOrDelete() }

    override suspend fun getTicker(coinId: String, quotesKey: String): CachedTicker? =
        dao.get(coinId, quotesKey)?.toCachedTickerOrDelete()

    override suspend fun upsertTicker(
        response: TickerResponse,
        quotesKey: String,
        fetchedAtEpochMillis: Long,
    ) {
        database.withTransaction {
            val current = dao.get(response.id, quotesKey)
            if (current == null || response.lastUpdated >= current.serverUpdatedAt) {
                dao.upsert(
                    TickerCacheEntity(
                        coinId = response.id,
                        quotesKey = quotesKey,
                        payload = json.encodeToString(TickerResponse.serializer(), response),
                        serverUpdatedAt = response.lastUpdated,
                        fetchedAtEpochMillis = fetchedAtEpochMillis,
                    )
                )
            }
        }
    }

    private suspend fun TickerCacheEntity.toCachedTickerOrDelete(): CachedTicker? =
        try {
            CachedTicker(
                response = json.decodeFromString(TickerResponse.serializer(), payload),
                fetchedAtEpochMillis = fetchedAtEpochMillis,
            )
        } catch (_: SerializationException) {
            dao.delete(coinId = coinId, quotesKey = quotesKey)
            null
        }
}
