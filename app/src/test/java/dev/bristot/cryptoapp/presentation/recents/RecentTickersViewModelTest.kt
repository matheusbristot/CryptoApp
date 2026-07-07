package dev.bristot.cryptoapp.presentation.recents

import dev.bristot.cryptoapp.data.repository.recents.RecentTickersRepositoryImpl
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import dev.bristot.cryptoapp.testutils.testTicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecentTickersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_emitsEmptyState() = runTest {
        val viewModel = RecentTickersViewModel(
            recentTickersRepository = RecentTickersRepositoryImpl(),
        )

        try {
            assertEquals(RecentTickersState(), viewModel.state.value)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun addRecentTicker_emitsUpdatedTickers() = runTest {
        val viewModel = RecentTickersViewModel(
            recentTickersRepository = RecentTickersRepositoryImpl(),
        )
        val bitcoin = testTicker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)

        viewModel.addRecentTicker(bitcoin)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val state = viewModel.state.first { state ->
                state.tickers.map { ticker -> ticker.id } == listOf("btc")
            }
            assertEquals(listOf(bitcoin), state.tickers)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun addRecentTicker_reordersExistingTickerToTop() = runTest {
        val viewModel = RecentTickersViewModel(
            recentTickersRepository = RecentTickersRepositoryImpl(),
        )
        val bitcoin = testTicker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val ethereum = testTicker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)

        viewModel.addRecentTicker(bitcoin)
        viewModel.addRecentTicker(ethereum)
        viewModel.addRecentTicker(bitcoin)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val state = viewModel.state.first { state ->
                state.tickers.map { ticker -> ticker.id } == listOf("btc", "eth")
            }
            assertEquals(listOf(bitcoin, ethereum), state.tickers)
        } finally {
            viewModel.clearForTest()
        }
    }
}
