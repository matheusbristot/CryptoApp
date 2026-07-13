package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.coins.presentation.CoinSortTemplate
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
    fun init_emitsSuccessStateWithCoinsSortedByRank() = runTest {
        val repository = FakeCoinRepository(
            coins = listOf(
                coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2),
                coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1),
            )
        )
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            coinRepository = repository,
            sortTemplate = CoinSortTemplate(),
        )

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
    fun init_whenRepositoryFails_emitsErrorState() = runTest {
        val repository = FakeCoinRepository(
            error = IllegalStateException("boom")
        )
        val viewModel = CoinListViewModel(
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
            coinRepository = repository,
            sortTemplate = CoinSortTemplate(),
        )

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
            coinRepository = repository,
            sortTemplate = CoinSortTemplate(),
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        return viewModel
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

    private class FakeCoinRepository(
        private val coins: List<Coin> = emptyList(),
        private val error: Throwable? = null,
    ) : CoinRepository {
        override suspend fun getCoins(): Flow<List<Coin>> = error?.let { throwable ->
            flow { throw throwable }
        } ?: flowOf(coins)
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }
}
