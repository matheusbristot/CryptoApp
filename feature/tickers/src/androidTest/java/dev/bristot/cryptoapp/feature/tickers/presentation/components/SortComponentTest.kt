package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.ui.sort.SortComponent
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SortComponentTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::SortComponentCombotAction,
        assert = ::SortComponentCombotAssert,
    )

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

        with(combotRule.arrangement) {
            action {
                selectNameSort()
            } assert {
                sortMenuIsClosed()
            }
        }

        assertEquals(SortType.NAME, receivedType)
        assertEquals(1, scrollRequests)
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

        with(combotRule.arrangement) {
            action { selectRankSort() }
        }

        assertEquals(0, callbackCount)
        assertEquals(0, scrollRequests)
    }
}
