package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TickersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshIfNeeded_emitsSuccessStateWithLoadedTickers() = runTest {
        val repository = FakeTickersRepository(
            tickers = listOf(
                ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 2),
                ticker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 1),
            )
        )
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = FakeFavoritesRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val successState = viewModel.state.first { it is TickersState.Success } as TickersState.Success
            assertEquals(
                listOf(
                    ticker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 1),
                    ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 2),
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
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = FakeFavoritesRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.state.first { it is TickersState.Success }

        viewModel.sortBy(SortState(type = SortType.NAME, order = SortOrder.ASCENDING))
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

    @Test
    fun changingSettings_waitsUntilNextVisibilityRefresh() = runTest {
        val repository = FakeTickersRepository(
            tickers = listOf(ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)),
        )
        val settingsRepository = FakeSettingsRepository()
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = settingsRepository,
            favoritesRepository = FakeFavoritesRepository(),
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
            assertEquals(listOf(setOf(CurrencySymbol.BRL)), repository.requests)

            viewModel.refreshIfNeeded()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                listOf(setOf(CurrencySymbol.BRL), setOf(CurrencySymbol.BRL, CurrencySymbol.USD)),
                repository.requests,
            )
            assertEquals(QuoteCurrency.USD, viewModel.quoteCurrency.value)
            val successState = viewModel.state.first { it is TickersState.Success }
                as TickersState.Success
            assertEquals(emptyList<Ticker>(), successState.tickers)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun reopeningWithoutSettingsChanges_reusesCurrentResult() = runTest {
        val repository = FakeTickersRepository(
            tickers = listOf(ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)),
        )
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = FakeFavoritesRepository(),
        )

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(listOf(setOf(CurrencySymbol.BRL)), repository.requests)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun existingFavorites_areBaselineAndKeepMarketSelected() = runTest {
        val favorite = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val repository = FakeTickersRepository(tickers = listOf(favorite)).apply {
            cachedTickers["btc"] = MutableStateFlow(favorite)
        }
        val favoritesRepository = FakeFavoritesRepository(initialIds = listOf("btc"))
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = favoritesRepository,
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(TickersSection.MARKET, viewModel.selectedSection.value)
            assertEquals(1, viewModel.favoritesState.value.count)
            assertEquals(listOf("btc"), viewModel.favoritesState.value.tickers.map { it.id })
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun addingThenRemovingLastFavorite_selectsAndRemovesFavoritesSection() = runTest {
        val favorite = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val repository = FakeTickersRepository(tickers = listOf(favorite)).apply {
            cachedTickers["btc"] = MutableStateFlow(favorite)
        }
        val favoritesRepository = FakeFavoritesRepository()
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = favoritesRepository,
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        favoritesRepository.replace("btc")
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TickersSection.FAVORITES, viewModel.selectedSection.value)
        assertEquals(1, viewModel.favoritesState.value.count)

        favoritesRepository.replace()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(TickersSection.MARKET, viewModel.selectedSection.value)
            assertEquals(0, viewModel.favoritesState.value.count)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun favoriteCacheAndQuoteChanges_areObservedAndRefreshed() = runTest {
        val brlTicker = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val usdTicker = brlTicker.copy(
            prices = mapOf(QuoteCurrency.USD to checkNotNull(brlTicker.prices[QuoteCurrency.BRL])),
        )
        val repository = FakeTickersRepository(tickers = listOf(brlTicker)).apply {
            cachedTickers["btc"] = MutableStateFlow(brlTicker)
        }
        val settingsRepository = FakeSettingsRepository()
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = settingsRepository,
            favoritesRepository = FakeFavoritesRepository(initialIds = listOf("btc")),
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        repository.cachedTickers.getValue("btc").value = brlTicker.copy(name = "Bitcoin synced")
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Bitcoin synced", viewModel.favoritesState.value.tickers.single().name)

        settingsRepository.replace(
            AppSettings(
                requestedQuoteCurrencies = setOf(QuoteCurrency.BRL, QuoteCurrency.USD),
                selectedQuoteCurrency = QuoteCurrency.USD,
            ),
        )
        repository.cachedTickers.getValue("btc").value = usdTicker
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(QuoteCurrency.USD, viewModel.quoteCurrency.value)
            assertEquals("btc", viewModel.favoritesState.value.tickers.single().id)
            assertEquals(
                Triple("btc", setOf(QuoteCurrency.BRL, QuoteCurrency.USD), false),
                repository.refreshRequests.single(),
            )
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun favoritesState_preservesLoadedAndUnavailableRefsWithPerItemError() = runTest {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val repository = FakeTickersRepository(tickers = listOf(bitcoin)).apply {
            cachedTickers["btc"] = MutableStateFlow(bitcoin)
            refreshFailures["missing"] = IllegalStateException("offline")
        }
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = FakeFavoritesRepository(initialIds = listOf("btc", "missing")),
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setActive(true)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            val state = viewModel.favoritesState.value
            assertEquals(2, state.items.size)
            assertEquals(bitcoin, state.items.first { it.ref.itemId == "btc" }.ticker)
            val unavailable = state.items.first { it.ref.itemId == "missing" }
            assertEquals(null, unavailable.ticker)
            assertEquals(false, unavailable.isLoading)
            assertEquals("offline", unavailable.error)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun favoriteRefsEmittedAfterActivation_areRefreshed() = runTest {
        val repository = FakeTickersRepository(tickers = emptyList())
        val favoritesRepository = FakeFavoritesRepository()
        val viewModel = TickersViewModel(
            tickersRepository = repository,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            sortTemplate = TickerSortTemplate(),
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = favoritesRepository,
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setActive(true)
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(emptyList<Triple<String, Set<CurrencySymbol>, Boolean>>(), repository.refreshRequests)

        favoritesRepository.replace("late")
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(
                listOf(Triple("late", setOf(QuoteCurrency.BRL), false)),
                repository.refreshRequests,
            )
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
        val requests = mutableListOf<Set<CurrencySymbol>>()
        val refreshRequests = mutableListOf<Triple<String, Set<CurrencySymbol>, Boolean>>()
        val cachedTickers = mutableMapOf<String, MutableStateFlow<Ticker?>>()
        val refreshFailures = mutableMapOf<String, Throwable>()

        override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> {
            requests += currencies
            return flowOf(tickers)
        }
        override suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker> = flowOf(tickers.first())

        override fun observeTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
        ): Flow<Ticker?> = cachedTickers.getOrPut(coinId) { MutableStateFlow(null) }

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
            force: Boolean,
        ) {
            refreshRequests += Triple(coinId, currencies, force)
            refreshFailures[coinId]?.let { throw it }
        }
    }

    private class FakeFavoritesRepository(initialIds: List<String> = emptyList()) : FavoritesRepository {
        private val refs = MutableStateFlow(initialIds.toRefs())

        override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> = refs

        override fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean> =
            refs.map { favorites -> favorites.any { it.itemId == itemId } }

        override suspend fun setFavorite(type: FavoriteType, itemId: String, isFavorite: Boolean) {
            val ids = refs.value.map { it.itemId }.toMutableSet()
            if (isFavorite) ids += itemId else ids -= itemId
            replace(*ids.toTypedArray())
        }

        fun replace(vararg ids: String) {
            refs.value = ids.toList().toRefs()
        }

        private fun List<String>.toRefs(): List<FavoriteRef> = mapIndexed { index, id ->
            FavoriteRef(FavoriteType.TICKER, id, index.toLong())
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val mutableSettings = MutableStateFlow(AppSettings())
        override val settings = mutableSettings

        fun replace(settings: AppSettings) {
            mutableSettings.value = settings
        }

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit

        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private class TestDispatcherProvider(
        override val main: kotlinx.coroutines.CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io = main
        override val default = main
    }
}
