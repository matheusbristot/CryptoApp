package dev.bristot.cryptoapp.feature.coins.presentation

import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListSection
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.FavoriteCoinItem
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

class CoinListControllerTest {

    @Test
    fun keepsStateAndDelegatesActions() {
        val state = MutableStateFlow<CoinListState>(CoinListState.Initial)
        val favorites = MutableStateFlow<List<FavoriteCoinItem>>(emptyList())
        val section = MutableStateFlow(CoinListSection.ALL)
        var shouldShowToTop = false
        var requestedSort: SortState? = null
        var refreshCount = 0
        val controller = CoinListController(
            state = state,
            favorites = favorites,
            selectedSection = section,
            refreshIfNeeded = { refreshCount++ },
            setActive = {},
            handleToTop = { shouldShowToTop = it },
            sortBy = { requestedSort = it },
            selectSection = { section.value = it },
        )
        val sort = SortState(
            type = SortType.NAME,
            order = SortOrder.DESCENDING,
        )

        assertSame(state, controller.state)
        controller.refreshIfNeeded()
        controller.handleToTop(true)
        controller.sortBy(sort)

        assertEquals(true, shouldShowToTop)
        assertEquals(1, refreshCount)
        assertEquals(sort, requestedSort)
        controller.handleToTop(false)
        assertFalse(shouldShowToTop)
    }
}
