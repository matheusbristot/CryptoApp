package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.coin_list.widgets.CoinListLoading
import dev.bristot.cryptoapp.feature.coins.presentation.coin_list.widgets.CoinListTile
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Rule
import org.junit.Test

class ComposeComponentsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun coinListLoading_showsProgressIndicator() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListLoading()
            }
        }

        composeRule.onNodeWithTag("coin_list_loading").assertIsDisplayed()
    }

    @Test
    fun coinListTile_displaysCoinInfo() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListTile(
                    coin = Coin(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        rank = 1,
                        isNew = false,
                        isActive = true,
                        type = "coin",
                    )
                )
            }
        }

        composeRule.onNodeWithTag("coin_tile_btc").assertIsDisplayed()
        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithText("BTC").assertIsDisplayed()
        composeRule.onNodeWithText("B").assertIsDisplayed()
    }
}
