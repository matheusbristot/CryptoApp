package dev.bristot.cryptoapp.presentation.controllers

import dev.bristot.cryptoapp.presentation.market_review.MarketReviewController
import dev.bristot.cryptoapp.presentation.market_review.MarketViewState
import dev.bristot.cryptoapp.presentation.tickers.TickersController
import dev.bristot.cryptoapp.presentation.tickers.TickersState
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ListState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ScrollStateSavable
import dev.bristot.cryptoapp.ui.widgets.sort.SortController
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortState
import dev.bristot.cryptoapp.ui.widgets.sort.SortType
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
    fun marketReviewController_keepsStateReference() {
        val state = MutableStateFlow<MarketViewState>(MarketViewState.Initial)

        val controller = MarketReviewController(state = state)

        assertSame(state, controller.state)
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

    @Test
    fun floatingButtonController_keepsStateAndCallbacks() {
        var visibilityCalledWith: Boolean? = null
        var savedIndex: Int? = null
        var savedOffset: Int? = null
        val state = MutableStateFlow(
            ScrollStateSavable(
                floatingButtonState = FloatingButtonState.Hidden,
                listState = ListState(),
            )
        )

        val controller = FloatingButtonController(
            state = state,
            onHandleVisibility = { visibilityCalledWith = it },
            onSaveScroll = { index, offset ->
                savedIndex = index
                savedOffset = offset
            },
        )

        controller.onHandleVisibility(true)
        controller.onSaveScroll(3, 42)

        assertSame(state, controller.state)
        assertEquals(true, visibilityCalledWith)
        assertEquals(3, savedIndex)
        assertEquals(42, savedOffset)
    }
}
