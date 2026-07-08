package dev.bristot.cryptoapp.feature.market_review.data.datasource

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.market_review.data.api.global.GlobalRoutes
import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketReviewRemoteDataSourceImplTest {

    private val dispatcher = StandardTestDispatcher()

    @Test
    fun getMarketOverviewData_emitsGlobalRouteResponse() = runTest(dispatcher) {
        val response = marketReviewResponse()
        val dataSource = MarketReviewRemoteDataSourceImpl(
            logger = FakeCryptoLogger(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            globalRoutes = FakeGlobalRoutes(response = response),
        )

        val result = dataSource.getMarketOverviewData().first()

        assertEquals(response, result)
    }

    @Test
    fun getMarketOverviewData_propagatesGlobalRouteFailures() = runTest(dispatcher) {
        val failure = IllegalStateException("route failed")
        val dataSource = MarketReviewRemoteDataSourceImpl(
            logger = FakeCryptoLogger(),
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            globalRoutes = FakeGlobalRoutes(failure = failure),
        )

        try {
            dataSource.getMarketOverviewData().first()
        } catch (exception: IllegalStateException) {
            assertEquals(failure, exception)
            return@runTest
        }

        throw AssertionError("Expected route failure")
    }

    private class FakeGlobalRoutes(
        private val response: MarketReviewResponse = marketReviewResponse(),
        private val failure: IllegalStateException? = null,
    ) : GlobalRoutes {
        override suspend fun getMarketReviewData(): MarketReviewResponse {
            failure?.let { throw it }
            return response
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

private fun marketReviewResponse() = MarketReviewResponse(
    marketCapUsd = 1_000_000L,
    volume24hUsd = 250_000L,
    bitcoinDominancePercentage = 54.3,
    cryptocurrenciesNumber = 10_500,
    marketCapAthValue = 2_000_000L,
    marketCapAthDate = "2026-01-01",
    volume24hAthValue = 500_000L,
    volume24hAthDate = "2026-02-01",
    marketCapChange24h = 1.25,
    volume24hChange24h = -0.75,
    lastUpdated = 1_725_000_000L,
)
