package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import org.junit.Rule
import org.junit.Test

class MarketReviewComponentTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        assert = ::MarketReviewComponentCombotAssert,
    )

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
                    quoteCurrency = "BRL",
                )
            }
        }

        with(combotRule.arrangement) {
            assert { marketStatsAreDisplayed() }
        }
    }

    @Test
    fun marketReviewComponent_withNoStats_keepsHeaderAndOmitsStatsRow() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketReviewComponent(
                    isDarkMode = false,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    stats = emptyList(),
                )
            }
        }

        with(combotRule.arrangement) {
            assert { headerIsDisplayedWithoutStats() }
        }
    }

    @Test
    fun marketReviewPlaceholder_matchesSectionContainer() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = true, dynamicColor = false) {
                MarketReviewPlaceholder(isDarkMode = true)
            }
        }

        with(combotRule.arrangement) {
            assert { placeholderIsDisplayed() }
        }
    }
}
