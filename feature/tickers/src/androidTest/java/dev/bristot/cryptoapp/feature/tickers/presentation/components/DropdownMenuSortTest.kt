package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.DropdownMenuSort
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortOrder
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortType
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
            DropdownMenuSort(
                expanded = true,
                onChangeSortType = { receivedType = it },
                onChangeSortOrder = { receivedOrder = it },
                onDismiss = { },
            )
        }

        composeRule.onNodeWithTag("sort_name").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("sort_desc").assertIsDisplayed().performClick()

        assertEquals(SortType.NAME, receivedType)
        assertEquals(SortOrder.DESCENDING, receivedOrder)
    }
}
