package dev.bristot.cryptoapp.feature.tickers.presentation.controllers

import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersState
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortController
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortOrder
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortState
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ControllersTest {

    @Test
    fun tickersController_keepsStateAndSortCallback() {
        var receivedSortType: SortType? = null
        var receivedSortOrder: SortOrder? = null
        val state = MutableStateFlow<TickersState>(TickersState.Initial)

        val controller = TickersController(
            state = state,
            sortBy = { sortType, sortOrder ->
                receivedSortType = sortType
                receivedSortOrder = sortOrder
            }
        )

        controller.sortBy(SortType.NAME, SortOrder.DESCENDING)

        assertSame(state, controller.state)
        assertEquals(SortType.NAME, receivedSortType)
        assertEquals(SortOrder.DESCENDING, receivedSortOrder)
    }

    @Test
    fun sortController_keepsStateAndCallbacks() {
        var receivedSortType: SortType? = null
        var receivedSortOrder: SortOrder? = null
        val state = MutableStateFlow(SortState(type = SortType.RANK, order = SortOrder.ASCENDING))

        val controller = SortController(
            state = state,
            changeType = { receivedSortType = it },
            changeOrder = { receivedSortOrder = it },
        )

        controller.changeType(SortType.SYMBOL)
        controller.changeOrder(SortOrder.DESCENDING)

        assertSame(state, controller.state)
        assertEquals(SortType.SYMBOL, receivedSortType)
        assertEquals(SortOrder.DESCENDING, receivedSortOrder)
    }
}
