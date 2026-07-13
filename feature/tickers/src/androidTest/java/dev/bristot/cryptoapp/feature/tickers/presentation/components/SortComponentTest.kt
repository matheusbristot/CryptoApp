package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.ui.sort.SortComponent
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SortComponentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun selectingDifferentType_invokesCallbackScrollsAndClosesMenu() {
        var receivedType: SortType? = null
        var scrollRequests = 0

        composeRule.setContent {
            SortComponent(
                state = SortState(),
                onChangeType = { receivedType = it },
                onChangeOrder = { },
                onScrollToFirstIndex = { scrollRequests++ },
            )
        }

        composeRule.onNodeWithContentDescription("Change sort").performClick()
        composeRule.onNodeWithTag("sort_name").performClick()

        assertEquals(SortType.NAME, receivedType)
        assertEquals(1, scrollRequests)
        composeRule.onAllNodesWithTag("dropdown_sort_menu").assertCountEquals(0)
    }

    @Test
    fun selectingCurrentType_doesNotInvokeCallbackOrScroll() {
        var callbackCount = 0
        var scrollRequests = 0

        composeRule.setContent {
            SortComponent(
                state = SortState(),
                onChangeType = { callbackCount++ },
                onChangeOrder = { },
                onScrollToFirstIndex = { scrollRequests++ },
            )
        }

        composeRule.onNodeWithContentDescription("Change sort").performClick()
        composeRule.onNodeWithTag("sort_rank").performClick()

        assertEquals(0, callbackCount)
        assertEquals(0, scrollRequests)
    }
}
