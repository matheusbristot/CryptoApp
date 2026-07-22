package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MoveToFirstTileFloatingButtonTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::MoveToFirstTileFloatingButtonCombotAction,
        assert = ::MoveToFirstTileFloatingButtonCombotAssert,
    )

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

        with(combotRule.arrangement) {
            assert {
                buttonIsClickable()
            } action {
                clickButton()
            }
        }

        assertEquals(listOf("show_bottom_bar", "scroll_to_top"), events)
    }
}
