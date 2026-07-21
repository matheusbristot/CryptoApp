package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.feature.tickers.testutils.testTicker
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TickerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refresh_observesCacheUpdatesWhileDetailIsActive() = runTest {
        val cached = MutableStateFlow<Ticker?>(
            testTicker("btc", "Bitcoin", "BTC", rank = 1, price = 10.0),
        )
        val repository = FakeTickersRepository(cached)
        val viewModel = viewModel(repository = repository)

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(10.0, (viewModel.state.value as TickerState.Success).ticker.prices
            .getValue(QuoteCurrency.BRL).price, 0.0)

        cached.value = testTicker("btc", "Bitcoin", "BTC", rank = 1, price = 12.0)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(12.0, (viewModel.state.value as TickerState.Success).ticker.prices
                .getValue(QuoteCurrency.BRL).price, 0.0)
            assertEquals(Triple("btc", setOf(QuoteCurrency.BRL), false), repository.refreshes.single())
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun toggleFavorite_updatesFavoriteEvenWhenTickerRefreshFails() = runTest {
        val favorites = FakeFavoritesRepository()
        val repository = FakeTickersRepository(MutableStateFlow(null), refreshError = "offline")
        val viewModel = viewModel(repository = repository, favoritesRepository = favorites)

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value is TickerState.Error)
        assertFalse(viewModel.isFavorite.value)

        viewModel.toggleFavorite()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.isFavorite.value)

        viewModel.toggleFavorite()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertFalse(viewModel.isFavorite.value)
            assertEquals(listOf(true, false), favorites.mutations)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun changedQuotes_restartCacheObservationAndRefresh() = runTest {
        val cached = MutableStateFlow<Ticker?>(testTicker("btc", "Bitcoin", "BTC", 1))
        val repository = FakeTickersRepository(cached)
        val settings = FakeSettingsRepository()
        val viewModel = viewModel(repository = repository, settingsRepository = settings)
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        settings.mutableSettings.value = AppSettings(
            requestedQuoteCurrencies = setOf(QuoteCurrency.BRL, QuoteCurrency.USD),
            selectedQuoteCurrency = QuoteCurrency.USD,
        )
        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(QuoteCurrency.USD, viewModel.quoteCurrency.value)
            assertEquals(
                listOf(setOf(QuoteCurrency.BRL), setOf(QuoteCurrency.BRL, QuoteCurrency.USD)),
                repository.observedQuotes,
            )
            assertEquals(2, repository.refreshes.size)
        } finally {
            viewModel.clearForTest()
        }
    }

    private fun viewModel(
        repository: TickersRepository,
        favoritesRepository: FavoritesRepository = FakeFavoritesRepository(),
        settingsRepository: SettingsRepository = FakeSettingsRepository(),
    ) = TickerViewModel(
        coinId = "btc",
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        tickersRepository = repository,
        settingsRepository = settingsRepository,
        favoritesRepository = favoritesRepository,
    )

    private class FakeTickersRepository(
        private val cached: MutableStateFlow<Ticker?>,
        private val refreshError: String? = null,
    ) : TickersRepository {
        val observedQuotes = mutableListOf<Set<CurrencySymbol>>()
        val refreshes = mutableListOf<Triple<String, Set<CurrencySymbol>, Boolean>>()

        override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> =
            flowOf(emptyList())

        override suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker> =
            flowOf(requireNotNull(cached.value))

        override fun observeTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker?> {
            observedQuotes += currencies
            return cached
        }

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<CurrencySymbol>,
            force: Boolean,
        ) {
            refreshes += Triple(coinId, currencies, force)
            refreshError?.let { error(it) }
        }
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        private val refs = MutableStateFlow<List<FavoriteRef>>(emptyList())
        val mutations = mutableListOf<Boolean>()

        override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> = refs

        override fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean> =
            refs.map { favorites -> favorites.any { it.itemId == itemId } }

        override suspend fun setFavorite(type: FavoriteType, itemId: String, isFavorite: Boolean) {
            mutations += isFavorite
            refs.value = if (isFavorite) {
                listOf(FavoriteRef(type, itemId, 1L))
            } else {
                emptyList()
            }
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        val mutableSettings = MutableStateFlow(AppSettings())
        override val settings = mutableSettings

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit

        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io = main
        override val default = main
    }
}
