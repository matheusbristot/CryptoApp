package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailComponent
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailController
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailState
import dev.bristot.cryptoapp.format.DefaultCryptoValueFormatter
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CoinDetailComponentTest {
    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::CoinDetailComponentCombotAction,
        assert = ::CoinDetailComponentCombotAssert,
    )

    @Test
    fun favoriteAction_remainsAvailableOnDetailError() {
        var toggled = false
        val controller = CoinDetailController(
            state = MutableStateFlow(
                CoinDetailState(isLoading = false, errorMessage = "offline"),
            ),
            refreshIfNeeded = {},
            toggleFavorite = { toggled = true },
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinDetailComponent(
                    name = "Bitcoin",
                    controller = controller,
                    valueFormatter = DefaultCryptoValueFormatter(),
                    showBackButton = true,
                    onBack = {},
                )
            }
        }

        with(combotRule.arrangement) {
            assert {
                errorIsDisplayed()
            } action {
                clickFavorite()
            }
        }
        composeRule.runOnIdle { assertTrue(toggled) }
    }
}
