package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortOrder
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TickersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_emitsSuccessStateWithLoadedTickers() = runTest {
        val repository = FakeTickersRepository(
            tickers = listOf(
                ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 2),
                ticker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 1),
            )
        )
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val successState = viewModel.state.first { it is TickersState.Success } as TickersState.Success
            assertEquals(
                listOf(
                    ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 2),
                    ticker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 1),
                ),
                successState.tickers
            )
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun sortBy_sortsLoadedTickersByRequestedField() = runTest {
        val repository = FakeTickersRepository(
            tickers = listOf(
                ticker(id = "b", name = "Beta", symbol = "B", rank = 2),
                ticker(id = "a", name = "Alpha", symbol = "A", rank = 1),
            )
        )
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.state.first { it is TickersState.Success }

        viewModel.sortBy(sortType = SortType.NAME, sortOrder = SortOrder.ASCENDING)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val successState = viewModel.state.first {
                it is TickersState.Success && it.tickers.map { ticker -> ticker.id } == listOf("a", "b")
            } as TickersState.Success
            assertEquals(listOf("a", "b"), successState.tickers.map { it.id })
        } finally {
            viewModel.clearForTest()
        }
    }

    private fun ticker(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
    ) = Ticker(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        prices = mapOf(
            CurrencySymbol.BRL to Currency(
                price = 1.0,
                volume24h = 1.0,
                volume24hChange24h = 1.0,
                marketCap = MarketCap(
                    marketCap = 1.0,
                    lastChangeTwentyFourHours = 1.0,
                ),
                percentChangeInterval = PercentChangeInterval(
                    p15m = 1.0,
                    p30m = 1.0,
                    p1h = 1.0,
                    p6h = 1.0,
                    p12h = 1.0,
                    p24h = 1.0,
                    p7d = 1.0,
                    p30d = 1.0,
                    p1y = 1.0,
                ),
                allTimeHigh = AllTimeHigh(
                    price = 1.0,
                    date = "2026-07-01",
                    percentage = 1.0,
                ),
            ),
        ),
    )

    private class FakeTickersRepository(
        private val tickers: List<Ticker>,
    ) : TickersRepository {
        override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> = flowOf(tickers)
        override suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker> = flowOf(tickers.first())
    }

    private class TestDispatcherProvider(
        override val main: kotlinx.coroutines.CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io = main
        override val default = main
    }
}
