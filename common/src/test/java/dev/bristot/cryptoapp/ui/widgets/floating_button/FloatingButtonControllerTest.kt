package dev.bristot.cryptoapp.ui.widgets.floating_button

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class FloatingButtonControllerTest {

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
