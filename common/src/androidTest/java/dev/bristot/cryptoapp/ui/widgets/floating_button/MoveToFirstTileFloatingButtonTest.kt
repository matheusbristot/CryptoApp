package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MoveToFirstTileFloatingButtonTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun moveToFirstTileFloatingButton_isClickable() {
        var clicked = false

        composeRule.setContent {
            MoveToFirstTileFloatingButton(onClick = { clicked = true })
        }

        composeRule.onNodeWithTag("move_to_first_tile_button").assertHasClickAction().performClick()

        assertEquals(true, clicked)
    }
}
