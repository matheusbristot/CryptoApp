package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListTile
import dev.bristot.cryptoapp.format.DefaultCryptoValueFormatter
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Rule
import org.junit.Test

class CoinListTileTest {
    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        assert = ::CoinListTileCombotAssert,
    )

    @Test
    fun coinListTile_displaysCoinInfo() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListTile(
                    valueFormatter = DefaultCryptoValueFormatter(),
                    coin = Coin(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        rank = 1,
                        isNew = false,
                        isActive = true,
                        type = "coin",
                    ),
                )
            }
        }

        with(combotRule.arrangement) {
            assert { bitcoinInfoIsDisplayed() }
        }
    }
}
