package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TickerTileTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tickerTile_displaysTickerInfo_andInvokesClick() {
        var clickedId = ""
        var clickedName = ""

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerTile(
                    isDarkMode = false,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ticker = ticker(),
                    valueFormatter = valueFormatter(),
                    onClick = { id, name ->
                        clickedId = id
                        clickedName = name
                    },
                )
            }
        }

        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed().assertHasClickAction().performClick()
        composeRule.onNodeWithTag("ticker_price").assertIsDisplayed()
        composeRule.onNodeWithText("+0.60%").assertIsDisplayed()
        composeRule.onNodeWithText("24h").assertIsDisplayed()

        assertEquals("btc", clickedId)
        assertEquals("Bitcoin", clickedName)
    }
}
