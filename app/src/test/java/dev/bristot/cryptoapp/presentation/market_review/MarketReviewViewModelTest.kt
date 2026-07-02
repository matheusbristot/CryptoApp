package dev.bristot.cryptoapp.presentation.market_review

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.domain.entity.MarketReview
import dev.bristot.cryptoapp.domain.repository.MarketReviewRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketReviewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_mapsRepositoryDataToMarketReviewState() = runTest {
        val repository = FakeMarketReviewRepository(
            marketReview = MarketReview(
                marketCapUsd = 1_500_000L,
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
        )
        val viewModel = MarketReviewViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            marketReviewRepository = repository,
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val marketReviewState = viewModel.state.first { it is MarketViewState.MarketReviewData } as MarketViewState.MarketReviewData
            assertEquals(
                MarketViewState.MarketReviewData(
                    data = listOf(
                        MarketStats(
                            label = "Total Market Cap",
                            value = "$1,5M",
                            change = "+1.25%",
                            isPositive = true,
                        ),
                        MarketStats(
                            label = "24h Volume",
                            value = "$250000",
                            change = "-0.75%",
                            isPositive = false,
                        ),
                    )
                ),
                marketReviewState
            )
        } finally {
            viewModel.clearForTest()
        }
    }

    private class FakeMarketReviewRepository(
        private val marketReview: MarketReview,
    ) : MarketReviewRepository {
        override suspend fun getMarketReviewData(): Flow<MarketReview> = flowOf(marketReview)
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }
}
