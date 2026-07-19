package dev.bristot.cryptoapp.feature.coins.data.sync

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class CoinSyncTaskTest {

    @Test
    fun sync_withoutTargets_finishesWithoutRepositoryRequests() = runTest {
        val repository = FakeCoinRepository()
        val task = task(repository = repository, ids = emptySet())

        assertEquals(SyncResult.Success, task.sync())
        assertEquals(emptyList<String>(), repository.requests)
        assertEquals(emptyList<Boolean>(), repository.forceValues)
    }

    @Test
    fun sync_withTransientFailure_requestsRetry() = runTest {
        val repository = FakeCoinRepository(
            failures = mapOf("btc-bitcoin" to IOException("offline")),
        )
        val task = task(repository = repository, ids = setOf("btc-bitcoin"))

        assertEquals(SyncResult.Retry, task.sync())
        assertEquals(listOf("btc-bitcoin"), repository.requests)
        assertEquals(listOf(false), repository.forceValues)
    }

    @Test
    fun sync_withNotFoundFailure_doesNotRequestRetry() = runTest {
        val repository = FakeCoinRepository(
            failures = mapOf(
                "missing-coin" to HttpException(
                    Response.error<Unit>(404, "missing".toResponseBody()),
                ),
            ),
        )
        val task = task(repository = repository, ids = setOf("missing-coin"))

        assertEquals(SyncResult.Success, task.sync())
        assertEquals(listOf("missing-coin"), repository.requests)
    }

    @Test
    fun sync_withPermanentFailure_continuesWithRemainingTargets() = runTest {
        val repository = FakeCoinRepository(
            failures = mapOf("broken-coin" to IllegalArgumentException("malformed")),
        )
        val task = task(
            repository = repository,
            ids = linkedSetOf("broken-coin", "btc-bitcoin"),
        )

        assertEquals(SyncResult.Failure, task.sync())
        assertEquals(listOf("broken-coin", "btc-bitcoin"), repository.requests)
        assertEquals(listOf(false, false), repository.forceValues)
    }

    private fun task(repository: FakeCoinRepository, ids: Set<String>) = CoinSyncTask(
        repository = repository,
        targetRegistry = object : SyncTargetRegistry {
            override suspend fun idsFor(type: SyncTargetType): Set<String> = ids
        },
        logger = NoOpLogger,
    )

    private class FakeCoinRepository(
        private val failures: Map<String, Throwable> = emptyMap(),
    ) : CoinRepository {
        val requests = mutableListOf<String>()
        val forceValues = mutableListOf<Boolean>()

        override suspend fun getCoins(): Flow<List<Coin>> = flowOf(emptyList())

        override fun observeCoin(coinId: String): Flow<Coin?> = flowOf(null)

        override suspend fun refreshCoin(coinId: String, force: Boolean) {
            requests += coinId
            forceValues += force
            failures[coinId]?.let { throw it }
        }
    }

    private data object NoOpLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}
