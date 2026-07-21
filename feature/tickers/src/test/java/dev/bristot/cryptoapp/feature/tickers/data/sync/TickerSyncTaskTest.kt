package dev.bristot.cryptoapp.feature.tickers.data.sync

import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class TickerSyncTaskTest {

    @Test
    fun sync_refreshesTargetsWithCurrentSettingsQuotes() = runTest {
        val repository = FakeTickersRepository()
        var requestedTargetType: SyncTargetType? = null
        val task = TickerSyncTask(
            repository = repository,
            settingsRepository = FakeSettingsRepository(
                AppSettings(
                    requestedQuoteCurrencies = setOf(QuoteCurrency.BRL, QuoteCurrency.USD),
                    selectedQuoteCurrency = QuoteCurrency.BRL,
                )
            ),
            targetRegistry = object : SyncTargetRegistry {
                override suspend fun idsFor(type: SyncTargetType): Set<String> {
                    requestedTargetType = type
                    return setOf("btc-bitcoin")
                }
            },
            logger = NoOpLogger,
        )

        assertEquals(SyncResult.Success, task.sync())
        assertEquals(SyncTargetType.TICKER, task.targetType)
        assertEquals(SyncTargetType.TICKER, requestedTargetType)
        assertEquals(
            listOf("btc-bitcoin" to setOf(QuoteCurrency.BRL, QuoteCurrency.USD)),
            repository.requests,
        )
        assertEquals(listOf(false), repository.forceValues)
    }

    @Test
    fun sync_withTransientFailure_requestsRetry() = runTest {
        val repository = FakeTickersRepository(
            failures = mapOf("btc-bitcoin" to IOException("offline")),
        )
        val task = task(repository = repository, ids = setOf("btc-bitcoin"))

        assertEquals(SyncResult.Retry, task.sync())
        assertEquals(listOf(false), repository.forceValues)
    }

    @Test
    fun sync_withNotFoundFailure_doesNotRequestRetry() = runTest {
        val repository = FakeTickersRepository(
            failures = mapOf(
                "missing-coin" to HttpException(
                    Response.error<Unit>(404, "missing".toResponseBody()),
                ),
            ),
        )
        val task = task(repository = repository, ids = setOf("missing-coin"))

        assertEquals(SyncResult.Success, task.sync())
    }

    @Test
    fun sync_withPermanentFailure_continuesWithRemainingTargets() = runTest {
        val repository = FakeTickersRepository(
            failures = mapOf("broken-coin" to IllegalArgumentException("malformed")),
        )
        val task = task(
            repository = repository,
            ids = linkedSetOf("broken-coin", "btc-bitcoin"),
        )

        assertEquals(SyncResult.Failure, task.sync())
        assertEquals(
            listOf(
                "broken-coin" to setOf(QuoteCurrency.BRL),
                "btc-bitcoin" to setOf(QuoteCurrency.BRL),
            ),
            repository.requests,
        )
        assertEquals(listOf(false, false), repository.forceValues)
    }

    private fun task(
        repository: FakeTickersRepository,
        ids: Set<String>,
    ) = TickerSyncTask(
        repository = repository,
        settingsRepository = FakeSettingsRepository(
            AppSettings(
                requestedQuoteCurrencies = setOf(QuoteCurrency.BRL),
                selectedQuoteCurrency = QuoteCurrency.BRL,
            )
        ),
        targetRegistry = object : SyncTargetRegistry {
            override suspend fun idsFor(type: SyncTargetType): Set<String> = ids
        },
        logger = NoOpLogger,
    )

    private class FakeTickersRepository(
        private val failures: Map<String, Throwable> = emptyMap(),
    ) : TickersRepository {
        val requests = mutableListOf<Pair<String, Set<CurrencySymbol>>>()
        val forceValues = mutableListOf<Boolean>()

        override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> =
            flowOf(emptyList())

        override suspend fun getTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
        ): Flow<Ticker> = error("Not used")

        override fun observeTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
        ): Flow<Ticker?> = flowOf(null)

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
            force: Boolean,
        ) {
            requests += coinId to currencies
            forceValues += force
            failures[coinId]?.let { throw it }
        }
    }

    private class FakeSettingsRepository(settingsValue: AppSettings) : SettingsRepository {
        override val settings = MutableStateFlow(settingsValue)

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit
        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private data object NoOpLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}
