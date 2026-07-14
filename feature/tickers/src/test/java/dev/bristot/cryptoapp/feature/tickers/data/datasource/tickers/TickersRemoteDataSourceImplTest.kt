package dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.tickers.data.api.tickers.TickersRoutes
import dev.bristot.cryptoapp.feature.tickers.data.model.CurrencyResponse
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TickersRemoteDataSourceImplTest {

    private val dispatcher = StandardTestDispatcher()

    @Test
    fun getTickers_emitsRouteResponse() = runTest(dispatcher) {
        val response = listOf(tickerResponse(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1))
        val routes = FakeTickersRoutes(tickers = response)
        val dataSource = TickersRemoteDataSourceImpl(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = FakeCryptoLogger(),
            tickersRoutes = routes,
        )

        val result = dataSource.getTickers(currencies = listOf("BRL", "BTC")).first()

        assertEquals("BRL,BTC", routes.requestedTickersQuotes)
        assertEquals(response, result)
    }

    @Test
    fun getTicker_emitsRouteResponse() = runTest(dispatcher) {
        val response = tickerResponse(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)
        val routes = FakeTickersRoutes(ticker = response)
        val dataSource = TickersRemoteDataSourceImpl(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = FakeCryptoLogger(),
            tickersRoutes = routes,
        )

        val result = dataSource.getTicker(
            coinId = "eth-ethereum",
            currencies = listOf("USD", "BTC"),
        ).first()

        assertEquals("eth-ethereum", routes.requestedTickerCoinId)
        assertEquals("USD,BTC", routes.requestedTickerQuotes)
        assertEquals(response, result)
    }

    @Test
    fun getTickers_propagatesRouteFailures() = runTest(dispatcher) {
        val failure = IllegalStateException("route failed")
        val dataSource = TickersRemoteDataSourceImpl(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            logger = FakeCryptoLogger(),
            tickersRoutes = FakeTickersRoutes(failure = failure),
        )

        try {
            dataSource.getTickers(currencies = listOf("BRL")).first()
        } catch (exception: IllegalStateException) {
            assertEquals(failure, exception)
            return@runTest
        }

        throw AssertionError("Expected route failure")
    }

    private class FakeTickersRoutes(
        private val tickers: List<TickerResponse> = emptyList(),
        private val ticker: TickerResponse = tickerResponse(),
        private val failure: IllegalStateException? = null,
    ) : TickersRoutes {
        var requestedTickersQuotes: String = ""
            private set
        var requestedTickerCoinId: String = ""
            private set
        var requestedTickerQuotes: String = ""
            private set

        override suspend fun getTickersByQuotes(quotes: String): List<TickerResponse> {
            failure?.let { throw it }
            requestedTickersQuotes = quotes
            return tickers
        }

        override suspend fun getTickerByQuotes(coinId: String, quotes: String): TickerResponse {
            failure?.let { throw it }
            requestedTickerCoinId = coinId
            requestedTickerQuotes = quotes
            return ticker
        }
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }

    private class FakeCryptoLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}

private fun tickerResponse(
    id: String = "btc",
    name: String = "Bitcoin",
    symbol: String = "BTC",
    rank: Int = 1,
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
    quotes = mapOf("BRL" to currencyResponse()),
)

private fun currencyResponse() = CurrencyResponse(
    price = 71_420.0,
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
