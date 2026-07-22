package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TickerTileTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::TickerTileCombotAction,
        assert = ::TickerTileCombotAssert,
    )

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

        with(combotRule.arrangement) {
            assert {
                tickerInfoIsDisplayed()
            } action {
                clickBitcoinTicker()
            }
        }

        assertEquals("btc", clickedId)
        assertEquals("Bitcoin", clickedName)
    }
}
