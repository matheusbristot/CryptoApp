package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingButtonManagerTest {

    @Test
    fun restoresStateFromSavedStateHandle() {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "floating_button_state" to FloatingButtonSavedState(
                    firstVisibleItemIndex = 2,
                    firstVisibleItemScrollOffset = 18,
                    isButtonVisible = true,
                )
            )
        )
        val manager = FloatingButtonManager(savedStateHandle)

        assertEquals(FloatingButtonState.Show, manager.state.value.floatingButtonState)
        assertEquals(2, manager.state.value.listState.firstVisibleItemIndex)
        assertEquals(18, manager.state.value.listState.firstVisibleItemScrollOffset)
    }

    @Test
    fun onHandleVisibility_updatesSavedStateAndButtonState() {
        val savedStateHandle = SavedStateHandle()
        val manager = FloatingButtonManager(savedStateHandle)

        manager.onHandleVisibility(showButton = true)

        assertEquals(FloatingButtonState.Show, manager.state.value.floatingButtonState)
        assertEquals(true, savedStateHandle.get<FloatingButtonSavedState>("floating_button_state")?.isButtonVisible)
    }

    @Test
    fun onSaveScroll_updatesSavedStateAndScrollPosition() {
        val savedStateHandle = SavedStateHandle()
        val manager = FloatingButtonManager(savedStateHandle)

        manager.onSaveScroll(index = 3, offset = 42)

        assertEquals(3, manager.state.value.listState.firstVisibleItemIndex)
        assertEquals(42, manager.state.value.listState.firstVisibleItemScrollOffset)
        assertEquals(3, savedStateHandle.get<FloatingButtonSavedState>("floating_button_state")?.firstVisibleItemIndex)
        assertEquals(42, savedStateHandle.get<FloatingButtonSavedState>("floating_button_state")?.firstVisibleItemScrollOffset)
    }
}
