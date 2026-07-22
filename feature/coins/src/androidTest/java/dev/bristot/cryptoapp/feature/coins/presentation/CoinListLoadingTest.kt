package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListLoading
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Rule
import org.junit.Test

class CoinListLoadingTest {
    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        assert = ::CoinListLoadingCombotAssert,
    )

    @Test
    fun coinListLoading_showsProgressIndicator() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListLoading()
            }
        }

        with(combotRule.arrangement) {
            assert { loadingIndicatorIsDisplayed() }
        }
    }
}
