package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MoveToFirstTileFloatingButtonTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun moveToFirstTileFloatingButton_isClickable() {
        val events = mutableListOf<String>()

        composeRule.setContent {
            CompositionLocalProvider(
                LocalScrollToTop provides { events += "show_bottom_bar" },
                LocalBottomBarPadding provides 24.dp,
            ) {
                MoveToFirstTileFloatingButton(onClick = { events += "scroll_to_top" })
            }
        }

        composeRule.onNodeWithTag("move_to_first_tile_button")
            .assertHasClickAction()
            .performClick()

        assertEquals(listOf("show_bottom_bar", "scroll_to_top"), events)
    }
}
