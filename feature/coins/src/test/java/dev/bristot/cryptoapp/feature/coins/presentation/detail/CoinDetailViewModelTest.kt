package dev.bristot.cryptoapp.feature.coins.presentation.detail

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.testutils.clearForTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoinDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun cachedMetadataAndPrice_areUpdatedReactively() = runTest {
        val coins = FakeCoinRepository(coin())
        val tickers = FakeTickersRepository(ticker(price = 350_000.0))
        val viewModel = viewModel(coins, tickers, FakeFavoritesRepository())
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(350_000.0, viewModel.state.value.coin?.quote?.price)

            tickers.value.value = ticker(price = 360_000.0)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(360_000.0, viewModel.state.value.coin?.quote?.price)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun toggleFavorite_worksEvenWithoutCachedData() = runTest {
        val favorites = FakeFavoritesRepository()
        val viewModel = viewModel(
            FakeCoinRepository(null),
            FakeTickersRepository(null),
            favorites,
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertFalse(viewModel.state.value.isFavorite)

            viewModel.toggleFavorite()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.isFavorite)
            assertEquals("btc", favorites.values.value.single().itemId)
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun refresh_usesBothRepositoriesWithoutForcingCache() = runTest {
        val coins = FakeCoinRepository(null)
        val tickers = FakeTickersRepository(null)
        val viewModel = viewModel(coins, tickers, FakeFavoritesRepository())

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(listOf(false), coins.refreshForces)
            assertEquals(
                listOf(Triple("btc", setOf(QuoteCurrency.BRL), false)),
                tickers.refreshes,
            )
        } finally {
            viewModel.clearForTest()
        }
    }

    @Test
    fun tickerRefreshFailure_withCachedMetadataRetriesOnNextActivation() = runTest {
        val coins = FakeCoinRepository(coin())
        val tickers = FakeTickersRepository(
            initial = null,
            refreshFailures = 1,
            tickerAfterSuccessfulRefresh = ticker(360_000.0),
        )
        val viewModel = viewModel(coins, tickers, FakeFavoritesRepository())

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("price offline", viewModel.state.value.errorMessage)
        assertEquals(1, tickers.refreshes.size)

        viewModel.refreshIfNeeded()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        try {
            assertEquals(2, tickers.refreshes.size)
            assertEquals(listOf(false, false), coins.refreshForces)
            assertEquals(360_000.0, viewModel.state.value.coin?.quote?.price)
            assertEquals(null, viewModel.state.value.errorMessage)
        } finally {
            viewModel.clearForTest()
        }
    }

    private fun viewModel(
        coins: FakeCoinRepository,
        tickers: FakeTickersRepository,
        favorites: FakeFavoritesRepository,
    ) = CoinDetailViewModel(
        coinId = "btc",
        dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        coinRepository = coins,
        tickersRepository = tickers,
        settingsRepository = FakeSettingsRepository(),
        favoritesRepository = favorites,
    )

    private fun coin() = Coin(
        id = "btc",
        name = "Bitcoin",
        symbol = "BTC",
        rank = 1,
        isNew = false,
        isActive = true,
        type = "coin",
    )

    private fun ticker(price: Double) = Ticker(
        id = "btc",
        name = "Bitcoin",
        symbol = "BTC",
        rank = 1,
        prices = mapOf(QuoteCurrency.BRL to currency(price)),
    )

    private fun currency(price: Double) = Currency(
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

    private class FakeCoinRepository(initial: Coin?) : CoinRepository {
        val value = MutableStateFlow(initial)
        val refreshForces = mutableListOf<Boolean>()

        override suspend fun getCoins(): Flow<List<Coin>> = flowOf(listOfNotNull(value.value))
        override fun observeCoin(coinId: String): Flow<Coin?> = value
        override suspend fun refreshCoin(coinId: String, force: Boolean) {
            refreshForces += force
        }
    }

    private class FakeTickersRepository(
        initial: Ticker?,
        private var refreshFailures: Int = 0,
        private val tickerAfterSuccessfulRefresh: Ticker? = null,
    ) : TickersRepository {
        val value = MutableStateFlow(initial)
        val refreshes = mutableListOf<Triple<String, Set<QuoteCurrency>, Boolean>>()

        override suspend fun getTickers(currencies: Set<QuoteCurrency>): Flow<List<Ticker>> =
            flowOf(listOfNotNull(value.value))

        override suspend fun getTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker> = flowOf(requireNotNull(value.value))

        override fun observeTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker?> = value

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
            force: Boolean,
        ) {
            refreshes += Triple(coinId, currencies, force)
            if (refreshFailures > 0) {
                refreshFailures--
                throw IOException("price offline")
            }
            tickerAfterSuccessfulRefresh?.let { value.value = it }
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        override val settings = MutableStateFlow(AppSettings())
        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) = Unit
        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) = Unit
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        val values = MutableStateFlow<List<FavoriteRef>>(emptyList())

        override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> = values
        override fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean> =
            values.map { refs -> refs.any { it.type == type && it.itemId == itemId } }

        override suspend fun setFavorite(type: FavoriteType, itemId: String, isFavorite: Boolean) {
            values.value = if (isFavorite) {
                listOf(FavoriteRef(type, itemId, 1L))
            } else {
                emptyList()
            }
        }
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }
}
