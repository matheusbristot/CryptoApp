package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.ui.sort.SortDropdownMenu
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DropdownMenuSortTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dropdownMenuSort_invokesCallbacksForTypeAndOrder() {
        var receivedType: SortType? = null
        var receivedOrder: SortOrder? = null

        composeRule.setContent {
            SortDropdownMenu(
                expanded = true,
                onChangeType = { receivedType = it },
                onChangeOrder = { receivedOrder = it },
                onDismiss = { },
            )
        }

        composeRule.onNodeWithTag("sort_name").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("sort_desc").assertIsDisplayed().performClick()

        assertEquals(SortType.NAME, receivedType)
        assertEquals(SortOrder.DESCENDING, receivedOrder)
    }
}
