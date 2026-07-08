package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Rule
import org.junit.Test

class MarketReviewComponentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun marketReviewComponent_displaysMarketStats() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketReviewComponent(
                    isDarkMode = false,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    stats = listOf(
                        MarketStats(
                            label = "Total Market Cap",
                            value = "\$1,5M",
                            change = "+1.25%",
                            isPositive = true,
                        ),
                        MarketStats(
                            label = "24h Volume",
                            value = "\$250000",
                            change = "-0.75%",
                            isPositive = false,
                        ),
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("market_review_component").assertIsDisplayed()
        composeRule.onNodeWithTag("market_stat_total_market_cap").assertIsDisplayed()
        composeRule.onNodeWithTag("market_stat_24h_volume").assertIsDisplayed()
        composeRule.onNodeWithText("Global Market").assertIsDisplayed()
        composeRule.onNodeWithText("Live").assertIsDisplayed()
        composeRule.onNodeWithText("\$1,5M").assertIsDisplayed()
        composeRule.onNodeWithText("\$250000").assertIsDisplayed()
    }
}
