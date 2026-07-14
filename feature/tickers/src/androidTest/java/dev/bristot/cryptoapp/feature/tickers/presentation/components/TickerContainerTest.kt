package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerContainer
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerController
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerState
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TickerContainerTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tickerContainer_showsLoadingAndBackButton() {
        var backClicked = false
        val state = MutableStateFlow<TickerState>(TickerState.Loading)
        val controller = TickerController(
            state = state,
            quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
            refreshIfNeeded = { },
        )

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Bitcoin",
                    tickerController = controller,
                    valueFormatter = valueFormatter(),
                    onBackButtonClick = { backClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("ticker_loading").assertIsDisplayed()

        assertEquals(true, backClicked)
    }

    @Test
    fun tickerContainer_showsErrorAndSuccessContent() {
        val loadingState = MutableStateFlow<TickerState>(TickerState.Error("An error occurred"))
        val controller = TickerController(
            state = loadingState,
            quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
            refreshIfNeeded = { },
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Ethereum",
                    tickerController = controller,
                    valueFormatter = valueFormatter(),
                    onBackButtonClick = { },
                )
            }
        }

        composeRule.onNodeWithText("Ethereum").assertIsDisplayed()
        composeRule.onNodeWithText("An error occurred").assertIsDisplayed()

        composeRule.runOnIdle {
            loadingState.value = TickerState.Success(ticker = ticker())
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
    }
}
