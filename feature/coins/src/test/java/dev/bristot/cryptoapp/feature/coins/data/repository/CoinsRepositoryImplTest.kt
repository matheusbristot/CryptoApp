package dev.bristot.cryptoapp.feature.coins.data.repository

import dev.bristot.cryptoapp.feature.coins.data.datasource.CoinsDatasource
import dev.bristot.cryptoapp.feature.coins.data.local.CoinCacheEntity
import dev.bristot.cryptoapp.feature.coins.data.local.CoinsLocalDataSource
import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.time.TimeProvider
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CoinsRepositoryImplTest {

    @Test
    fun getCoins_filtersInactiveAndNonCoinItemsAndMapsToDomain() = runBlocking {
        val dataSource = FakeCoinsDatasource(
            coins = listOf(
                coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1, isActive = true, type = "coin"),
                coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2, isActive = false, type = "coin"),
                coin(id = "usd", name = "USD Coin", symbol = "USDC", rank = 3, isActive = true, type = "token"),
            )
        )
        val repository = repository(dataSource = dataSource)

        val coins = repository.getCoins().first()

        assertEquals(
            listOf(
                Coin(
                    id = "btc",
                    name = "Bitcoin",
                    symbol = "BTC",
                    rank = 1,
                    isNew = false,
                    isActive = true,
                    type = "coin",
                )
            ),
            coins
        )
    }

    @Test
    fun refreshCoin_skipsNetworkWhenCacheIsFresh() = runBlocking {
        val local = FakeCoinsLocalDataSource(
            initial = cacheEntity(id = "btc", fetchedAtEpochMillis = 950_000L),
        )
        val remote = FakeCoinsDatasource(coinById = coin(id = "btc"))
        val repository = repository(
            dataSource = remote,
            localDataSource = local,
            now = 1_000_000L,
        )

        repository.refreshCoin(coinId = "btc")

        assertEquals(0, remote.coinRequestCount)
    }

    @Test
    fun refreshCoin_updatesStaleCacheAndObserveCoinEmitsDomain() = runBlocking {
        val local = FakeCoinsLocalDataSource(
            initial = cacheEntity(id = "btc", name = "Old Bitcoin", fetchedAtEpochMillis = 1L),
        )
        val remote = FakeCoinsDatasource(
            coinById = coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1),
        )
        val repository = repository(
            dataSource = remote,
            localDataSource = local,
            now = 100_000L,
        )

        repository.refreshCoin(coinId = "btc")

        assertEquals(1, remote.coinRequestCount)
        assertEquals("Bitcoin", repository.observeCoin("btc").first()?.name)
        assertEquals(100_000L, local.value.value?.fetchedAtEpochMillis)
    }

    @Test
    fun refreshCoin_networkFailurePreservesStaleCache() = runBlocking {
        val local = FakeCoinsLocalDataSource(
            initial = cacheEntity(
                id = "btc",
                name = "Last known Bitcoin",
                fetchedAtEpochMillis = 1L,
            ),
        )
        val remote = FakeCoinsDatasource(coinFailure = IOException("offline"))
        val repository = repository(
            dataSource = remote,
            localDataSource = local,
            now = 100_000L,
        )

        assertThrows(IOException::class.java) {
            runBlocking { repository.refreshCoin(coinId = "btc") }
        }

        assertEquals(1, remote.coinRequestCount)
        assertEquals("Last known Bitcoin", repository.observeCoin("btc").first()?.name)
        assertEquals(1L, local.value.value?.fetchedAtEpochMillis)
    }

    private fun repository(
        dataSource: FakeCoinsDatasource = FakeCoinsDatasource(),
        localDataSource: FakeCoinsLocalDataSource = FakeCoinsLocalDataSource(),
        now: Long = 1_000_000L,
    ) = CoinsRepositoryImpl(
        coinsDatasource = dataSource,
        localDataSource = localDataSource,
        timeProvider = TimeProvider { now },
    )

    private fun coin(
        id: String,
        name: String = "Bitcoin",
        symbol: String = "BTC",
        rank: Int = 1,
        isActive: Boolean = true,
        type: String = "coin",
    ) = CoinResponse(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        isNew = false,
        isActive = isActive,
        type = type,
    )

    private fun cacheEntity(
        id: String,
        name: String = "Bitcoin",
        fetchedAtEpochMillis: Long,
    ) = CoinCacheEntity(
        id = id,
        name = name,
        symbol = "BTC",
        rank = 1,
        isNew = false,
        isActive = true,
        type = "coin",
        fetchedAtEpochMillis = fetchedAtEpochMillis,
    )

    private class FakeCoinsDatasource(
        private val coins: List<CoinResponse> = emptyList(),
        private val coinById: CoinResponse = CoinResponse(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            rank = 1,
            isNew = false,
            isActive = true,
            type = "coin",
        ),
        private val coinFailure: Throwable? = null,
    ) : CoinsDatasource {
        var coinRequestCount: Int = 0
            private set

        override suspend fun getCoins(): Flow<List<CoinResponse>> = flowOf(coins)

        override suspend fun getCoin(coinId: String): CoinResponse {
            coinRequestCount++
            coinFailure?.let { throw it }
            return coinById
        }
    }

    private class FakeCoinsLocalDataSource(
        initial: CoinCacheEntity? = null,
    ) : CoinsLocalDataSource {
        val value = MutableStateFlow(initial)

        override fun observeCoin(coinId: String): Flow<CoinCacheEntity?> = value

        override suspend fun getCoin(coinId: String): CoinCacheEntity? = value.value

        override suspend fun upsertCoin(entity: CoinCacheEntity) {
            value.value = entity
        }
    }
}
