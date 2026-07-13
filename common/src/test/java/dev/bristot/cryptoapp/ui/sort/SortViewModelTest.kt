package dev.bristot.cryptoapp.ui.sort

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class SortViewModelTest {

    @Test
    fun state_startsWithDefaultAndUpdatesDistinctValues() {
        val viewModel = SortViewModel(SavedStateHandle())

        assertEquals(SortState(), viewModel.state.value)
        viewModel.changeType(SortType.NAME)
        viewModel.changeOrder(SortOrder.DESCENDING)
        viewModel.changeOrder(SortOrder.DESCENDING)

        assertEquals(SortState(SortType.NAME, SortOrder.DESCENDING), viewModel.state.value)
    }

    @Test
    fun state_restoresFromSavedStateHandle() {
        val restored = SortState(SortType.SYMBOL, SortOrder.DESCENDING)
        val viewModel = SortViewModel(SavedStateHandle(mapOf("sort_config" to restored)))

        assertEquals(restored, viewModel.state.value)
    }
}
