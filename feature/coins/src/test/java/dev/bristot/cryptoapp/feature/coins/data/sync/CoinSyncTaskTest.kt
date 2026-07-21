package dev.bristot.cryptoapp.feature.coins.data.sync

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
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
    fun sync_refreshesMetadataAndPricesWithRequestedSettingsQuotes() = runTest {
        val repository = FakeCoinRepository()
        val tickersRepository = FakeTickersRepository()
        val task = task(
            repository = repository,
            ids = setOf("btc-bitcoin"),
            tickersRepository = tickersRepository,
            settings = AppSettings(
                requestedQuoteCurrencies = setOf(QuoteCurrency.BRL, QuoteCurrency.USD),
                selectedQuoteCurrency = QuoteCurrency.BRL,
            ),
        )

        assertEquals(SyncResult.Success, task.sync())
        assertEquals(listOf("btc-bitcoin"), repository.requests)
        assertEquals(
            listOf(Triple("btc-bitcoin", setOf(QuoteCurrency.BRL, QuoteCurrency.USD), false)),
            tickersRepository.requests,
        )
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

    private fun task(
        repository: FakeCoinRepository,
        ids: Set<String>,
        tickersRepository: FakeTickersRepository = FakeTickersRepository(),
        settings: AppSettings = AppSettings(),
    ) = CoinSyncTask(
        repository = repository,
        tickersRepository = tickersRepository,
        settingsRepository = FakeSettingsRepository(settings),
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

    private class FakeTickersRepository : TickersRepository {
        val requests = mutableListOf<Triple<String, Set<QuoteCurrency>, Boolean>>()

        override suspend fun getTickers(currencies: Set<QuoteCurrency>): Flow<List<Ticker>> =
            flowOf(emptyList())

        override suspend fun getTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker> = flowOf()

        override fun observeTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker?> = flowOf(null)

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
            force: Boolean,
        ) {
            requests += Triple(coinId, currencies, force)
        }
    }

    private class FakeSettingsRepository(initial: AppSettings) : SettingsRepository {
        override val settings = MutableStateFlow(initial)

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit
        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private data object NoOpLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}
