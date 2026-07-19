package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers.TickersDataSource
import dev.bristot.cryptoapp.feature.tickers.data.model.CurrencyResponse
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.feature.tickers.data.local.CachedTicker
import dev.bristot.cryptoapp.feature.tickers.data.local.TickersLocalDataSource
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import dev.bristot.cryptoapp.time.TimeProvider

class TickersRepositoryImplTest {

    @Test
    fun getTickers_passesCurrencyNamesToDataSourceAndMapsResponse() = runBlocking {
        val dataSource = FakeTickersDataSource(
            tickers = listOf(tickerResponse(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)),
            ticker = tickerResponse(id = "placeholder", name = "placeholder", symbol = "P", rank = 0),
        )
        val repository = repository(dataSource = dataSource)

        val tickers = repository.getTickers(
            currencies = setOf(CurrencySymbol.BRL, CurrencySymbol.BTC)
        ).first()

        assertEquals(listOf("BRL", "BTC"), dataSource.requestedTickersCurrencies)
        assertEquals(
            listOf(
                Ticker(
                    id = "btc",
                    name = "Bitcoin",
                    symbol = "BTC",
                    rank = 1,
                    totalSupply = 1_000L,
                    maxSupply = 2_000L,
                    betaValue = 1.0,
                    firstDataAt = "2026-07-01T00:00:00Z",
                    lastUpdated = "2026-07-01T00:00:00Z",
                    prices = mapOf(
                        CurrencySymbol.BRL to expectedCurrency(price = 71420.0),
                        CurrencySymbol.BTC to expectedCurrency(price = 1.0),
                    ),
                )
            ),
            tickers
        )
    }

    @Test
    fun getTicker_passesCurrencyNamesToDataSourceAndMapsResponse() = runBlocking {
        val dataSource = FakeTickersDataSource(
            ticker = tickerResponse(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)
        )
        val repository = repository(dataSource = dataSource)

        val ticker = repository.getTicker(coinId = "eth-ethereum", currencies = setOf(CurrencySymbol.USD)).first()

        assertEquals("eth-ethereum", dataSource.requestedTickerCoinId)
        assertEquals(listOf("USD"), dataSource.requestedTickerCurrencies)
        assertEquals(
            Ticker(
                id = "eth",
                name = "Ethereum",
                symbol = "ETH",
                rank = 2,
                totalSupply = 1_000L,
                maxSupply = 2_000L,
                betaValue = 1.0,
                firstDataAt = "2026-07-01T00:00:00Z",
                lastUpdated = "2026-07-01T00:00:00Z",
                prices = mapOf(
                    CurrencySymbol.USD to expectedCurrency(price = 2500.0),
                ),
            ),
            ticker
        )
    }

    @Test
    fun refreshTicker_skipsNetworkWhenCacheIsFresh() = runBlocking {
        val cachedResponse = tickerResponse(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val local = FakeTickersLocalDataSource(
            initial = CachedTicker(
                response = cachedResponse,
                fetchedAtEpochMillis = 950_000L,
            )
        )
        val dataSource = FakeTickersDataSource(ticker = cachedResponse)
        val repository = repository(
            dataSource = dataSource,
            localDataSource = local,
            now = 1_000_000L,
        )

        repository.refreshTicker(
            coinId = "btc",
            currencies = setOf(CurrencySymbol.USD),
        )

        assertEquals(0, dataSource.tickerRequestCount)
    }

    @Test
    fun refreshTicker_updatesStaleCacheOnce() = runBlocking {
        val local = FakeTickersLocalDataSource(
            initial = CachedTicker(
                response = tickerResponse(
                    id = "btc",
                    name = "Old Bitcoin",
                    symbol = "BTC",
                    rank = 1,
                ),
                fetchedAtEpochMillis = 1L,
            )
        )
        val response = tickerResponse(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val dataSource = FakeTickersDataSource(ticker = response)
        val repository = repository(
            dataSource = dataSource,
            localDataSource = local,
            now = 1_000_000L,
        )

        repository.refreshTicker("btc", setOf(CurrencySymbol.USD))

        assertEquals(1, dataSource.tickerRequestCount)
        assertEquals("Bitcoin", local.current?.response?.name)
        assertEquals(1_000_000L, local.current?.fetchedAtEpochMillis)
    }

    @Test
    fun refreshTicker_networkFailurePreservesStaleCache() = runBlocking {
        val cached = CachedTicker(
            response = tickerResponse(
                id = "btc",
                name = "Last known Bitcoin",
                symbol = "BTC",
                rank = 1,
            ),
            fetchedAtEpochMillis = 1L,
        )
        val local = FakeTickersLocalDataSource(initial = cached)
        val dataSource = FakeTickersDataSource(
            ticker = cached.response,
            tickerFailure = IOException("offline"),
        )
        val repository = repository(
            dataSource = dataSource,
            localDataSource = local,
            now = 1_000_000L,
        )

        assertThrows(IOException::class.java) {
            runBlocking { repository.refreshTicker("btc", setOf(CurrencySymbol.USD)) }
        }

        assertEquals(1, dataSource.tickerRequestCount)
        assertEquals(cached, local.current)
    }

    private fun repository(
        dataSource: FakeTickersDataSource,
        localDataSource: FakeTickersLocalDataSource = FakeTickersLocalDataSource(),
        now: Long = 1_000_000L,
    ) = TickersRepositoryImpl(
        tickersDataSource = dataSource,
        localDataSource = localDataSource,
        timeProvider = TimeProvider { now },
    )

    private fun tickerResponse(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
    ) = TickerResponse(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        totalSupply = 1_000L,
        maxSupply = 2_000L,
        betaValue = 1.0,
        firstDataAt = "2026-07-01T00:00:00Z",
        lastUpdated = "2026-07-01T00:00:00Z",
        quotes = mapOf(
            "BRL" to currencyResponse(price = 71420.0),
            "BTC" to currencyResponse(price = 1.0),
            "USD" to currencyResponse(price = 2500.0),
        ),
    )

    private fun currencyResponse(price: Double) = CurrencyResponse(
        price = price,
        volume24h = 100.0,
        volume24hChange24h = 1.5,
        marketCap = 1_000.0,
        marketCapChange24h = 2.5,
        percentChange15m = 0.1,
        percentChange30m = 0.2,
        percentChange1h = 0.3,
        percentChange6h = 0.4,
        percentChange12h = 0.5,
        percentChange24h = 0.6,
        percentChange7d = 0.7,
        percentChange30d = 0.8,
        percentChange1y = 0.9,
        athPrice = 3_000.0,
        athDate = "2026-01-01T00:00:00Z",
        percentFromPriceAth = -10.0,
    )

    private fun expectedCurrency(price: Double) = dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency(
        price = price,
        volume24h = 100.0,
        volume24hChange24h = 1.5,
        marketCap = dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap(
            marketCap = 1_000.0,
            lastChangeTwentyFourHours = 2.5,
        ),
        percentChangeInterval = dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval(
            p15m = 0.1,
            p30m = 0.2,
            p1h = 0.3,
            p6h = 0.4,
            p12h = 0.5,
            p24h = 0.6,
            p7d = 0.7,
            p30d = 0.8,
            p1y = 0.9,
        ),
        allTimeHigh = dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh(
            price = 3_000.0,
            date = "2026-01-01T00:00:00Z",
            percentage = -10.0,
        ),
    )

    private class FakeTickersDataSource(
        private val tickers: List<TickerResponse> = emptyList(),
        private val ticker: TickerResponse,
        private val tickerFailure: Throwable? = null,
    ) : TickersDataSource {
        var requestedTickersCurrencies: List<String> = emptyList()
            private set
        var requestedTickerCoinId: String = ""
            private set
        var requestedTickerCurrencies: List<String> = emptyList()
            private set
        var tickerRequestCount: Int = 0
            private set

        override suspend fun getTickers(currencies: List<String>): Flow<List<TickerResponse>> {
            requestedTickersCurrencies = currencies
            return flowOf(tickers)
        }

        override suspend fun getTicker(coinId: String, currencies: List<String>): Flow<TickerResponse> {
            tickerRequestCount++
            requestedTickerCoinId = coinId
            requestedTickerCurrencies = currencies
            tickerFailure?.let { throw it }
            return flowOf(ticker)
        }
    }

    private class FakeTickersLocalDataSource(
        initial: CachedTicker? = null,
    ) : TickersLocalDataSource {
        private val value = MutableStateFlow(initial)
        val current: CachedTicker?
            get() = value.value

        override fun observeTicker(coinId: String, quotesKey: String): Flow<CachedTicker?> = value

        override suspend fun getTicker(coinId: String, quotesKey: String): CachedTicker? = value.value

        override suspend fun upsertTicker(
            response: TickerResponse,
            quotesKey: String,
            fetchedAtEpochMillis: Long,
        ) {
            value.value = CachedTicker(
                response = response,
                fetchedAtEpochMillis = fetchedAtEpochMillis,
            )
        }
    }
}
