package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.ui.sort.SortDropdownMenu
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DropdownMenuSortTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::DropdownMenuSortCombotAction,
        assert = ::DropdownMenuSortCombotAssert,
    )

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

        with(combotRule.arrangement) {
            assert {
                sortOptionsAreDisplayed()
            } action {
                selectNameDescending()
            }
        }

        assertEquals(SortType.NAME, receivedType)
        assertEquals(SortOrder.DESCENDING, receivedOrder)
    }
}
