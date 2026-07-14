package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.coins.domain.usecase.GetQuotedCoinsUseCase
import dev.bristot.cryptoapp.feature.coins.presentation.CoinSortTemplate
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshIfNeeded_emitsSuccessStateWithCoinsSortedByRank() = runTest {
        val repository = FakeCoinRepository(
            coins = listOf(
                coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2),
                coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1),
            )
        )
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            getQuotedCoins = quotedCoinsUseCase(
                coinRepository = repository,
                tickersRepository = FakeTickersRepository(),
            ),
            sortTemplate = CoinSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val state = viewModel.state.first { it is CoinListState.SuccessWithUIProperties } as CoinListState.SuccessWithUIProperties
            assertEquals(listOf("btc", "eth"), state.coins.map { it.id })
            assertTrue(state.toTopVisibility.not())
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun handleToTop_updatesTopButtonVisibility() = runTest {
        val viewModel = buildLoadedViewModel()

        try {
            viewModel.handleToTop(shouldShow = true)

            val state = viewModel.state.first { it is CoinListState.SuccessWithUIProperties } as CoinListState.SuccessWithUIProperties
            assertTrue(state.toTopVisibility)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun sortBy_sortsCoinsBySelectedField() = runTest {
        val viewModel = buildLoadedViewModel(
            coins = listOf(
                coin(id = "b", name = "Beta", symbol = "B", rank = 2),
                coin(id = "a", name = "Alpha", symbol = "A", rank = 1),
                coin(id = "c", name = "Charlie", symbol = "C", rank = 3),
            )
        )

        try {
            viewModel.sortBy(SortState(type = SortType.NAME))

            val state = viewModel.state.first { it is CoinListState.SuccessWithUIProperties } as CoinListState.SuccessWithUIProperties
            assertEquals(listOf("a", "b", "c"), state.coins.map { it.id })
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun sortBy_updatesSortOrderAndResortsCurrentCoins() = runTest {
        val viewModel = buildLoadedViewModel(
            coins = listOf(
                coin(id = "b", name = "Beta", symbol = "B", rank = 2),
                coin(id = "a", name = "Alpha", symbol = "A", rank = 1),
            )
        )

        try {
            viewModel.sortBy(SortState(order = SortOrder.DESCENDING))

            val state = viewModel.state.first { it is CoinListState.SuccessWithUIProperties } as CoinListState.SuccessWithUIProperties
            assertEquals(listOf("b", "a"), state.coins.map { it.id })
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun refreshIfNeeded_whenRepositoryFails_emitsErrorState() = runTest {
        val repository = FakeCoinRepository(
            error = IllegalStateException("boom")
        )
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            getQuotedCoins = quotedCoinsUseCase(
                coinRepository = repository,
                tickersRepository = FakeTickersRepository(),
            ),
            sortTemplate = CoinSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val errorState = viewModel.state.first { it is CoinListState.Error } as CoinListState.Error
            assertEquals("An error occurred", errorState.message)
        } finally {
            viewModel.clearForTest()
        }
    }

    private fun buildLoadedViewModel(
        coins: List<Coin> = listOf(
            coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1),
            coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2),
        )
    ): CoinListViewModel {
        val repository = FakeCoinRepository(coins = coins)
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            getQuotedCoins = quotedCoinsUseCase(
                coinRepository = repository,
                tickersRepository = FakeTickersRepository(),
            ),
            sortTemplate = CoinSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
        )
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        return viewModel
    }

    @Test
    fun selectedQuoteWaitsUntilNextVisibilityRefresh() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val tickersRepository = FakeTickersRepository(
            tickerPrices = mapOf(
                QuoteCurrency.BRL to 350_000.0,
                QuoteCurrency.USD to 65_000.0,
            )
        )
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            getQuotedCoins = quotedCoinsUseCase(
                coinRepository = FakeCoinRepository(
                    coins = listOf(coin("btc", "Bitcoin", "BTC", 1))
                ),
                tickersRepository = tickersRepository,
            ),
            sortTemplate = CoinSortTemplate(),
            settingsRepository = settingsRepository,
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        settingsRepository.replace(
            AppSettings(
                requestedQuoteCurrencies = setOf(QuoteCurrency.BRL, QuoteCurrency.USD),
                selectedQuoteCurrency = QuoteCurrency.USD,
            )
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(listOf(setOf(QuoteCurrency.BRL)), tickersRepository.requests)

            viewModel.refreshIfNeeded()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.first { current ->
                current is CoinListState.SuccessWithUIProperties &&
                    current.coins.first().quote?.currency == QuoteCurrency.USD
            } as CoinListState.SuccessWithUIProperties
            assertEquals(65_000.0, state.coins.first().quote?.price)
            assertEquals(
                listOf(setOf(QuoteCurrency.BRL), setOf(QuoteCurrency.USD)),
                tickersRepository.requests,
            )
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun reopeningWithoutQuoteChange_reusesCurrentResult() = runTest {
        val tickersRepository = FakeTickersRepository()
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            getQuotedCoins = quotedCoinsUseCase(
                coinRepository = FakeCoinRepository(
                    coins = listOf(coin("btc", "Bitcoin", "BTC", 1))
                ),
                tickersRepository = tickersRepository,
            ),
            sortTemplate = CoinSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(listOf(setOf(QuoteCurrency.BRL)), tickersRepository.requests)
        } finally {
            viewModel.clearForTest()
        }
    }

    private fun coin(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        isNew = false,
        isActive = true,
        type = "coin",
    )

    private fun quotedCoinsUseCase(
        coinRepository: CoinRepository,
        tickersRepository: TickersRepository,
    ) = GetQuotedCoinsUseCase(
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        coinRepository = coinRepository,
        tickersRepository = tickersRepository,
    )

    private class FakeCoinRepository(
        private val coins: List<Coin> = emptyList(),
        private val error: Throwable? = null,
    ) : CoinRepository {
        override suspend fun getCoins(): Flow<List<Coin>> = error?.let { throwable ->
            flow { throw throwable }
        } ?: flowOf(coins)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val mutableSettings = MutableStateFlow(AppSettings())
        override val settings = mutableSettings

        fun replace(value: AppSettings) {
            mutableSettings.value = value
        }

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit
        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private class FakeTickersRepository(
        private val tickerPrices: Map<QuoteCurrency, Double> = emptyMap(),
    ) : TickersRepository {
        val requests = mutableListOf<Set<QuoteCurrency>>()

        override suspend fun getTickers(currencies: Set<QuoteCurrency>): Flow<List<Ticker>> {
            requests += currencies
            if (tickerPrices.isEmpty()) return flowOf(emptyList())
            return flowOf(
                listOf(
                    Ticker(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        rank = 1,
                        prices = currencies.mapNotNull { currency ->
                            tickerPrices[currency]?.let { price -> currency to tickerCurrency(price) }
                        }.toMap(),
                    )
                )
            )
        }

        override suspend fun getTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker> = getTickers(currencies).let { flow ->
            kotlinx.coroutines.flow.flow { emit(flow.first().first()) }
        }

        private fun tickerCurrency(price: Double) = Currency(
            price = price,
            volume24h = 1.0,
            volume24hChange24h = 0.0,
            marketCap = MarketCap(1.0, 0.0),
            percentChangeInterval = PercentChangeInterval(
                p15m = 0.0,
                p30m = 0.0,
                p1h = 0.0,
                p6h = 0.0,
                p12h = 0.0,
                p24h = 0.0,
                p7d = 0.0,
                p30d = 0.0,
                p1y = 0.0,
            ),
            allTimeHigh = AllTimeHigh(null, null, null),
        )
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }
}
