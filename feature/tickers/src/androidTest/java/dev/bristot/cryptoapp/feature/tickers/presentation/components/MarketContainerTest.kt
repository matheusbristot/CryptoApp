package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersState
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.MarketContainer
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersState
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketContainerTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun marketContainer_showsRecentSectionAndFiltersOnlyDisplayedRecentTickers() {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val ethereum = ticker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)
        val solana = ticker(id = "sol", name = "Solana", symbol = "SOL", rank = 3)
        val cardano = ticker(id = "ada", name = "Cardano", symbol = "ADA", rank = 4)
        val xrp = ticker(id = "xrp", name = "XRP", symbol = "XRP", rank = 5)
        var openRecentsClicked = false
        var selectedTicker: Ticker? = null

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(
                            TickersState.Success(
                                tickers = listOf(bitcoin, ethereum, solana, cardano, xrp),
                            )
                        ),
                        sortBy = { },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(
                            RecentTickersState(
                                tickers = listOf(bitcoin, ethereum, solana, cardano),
                            )
                        ),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _ -> Text("Market overview") },
                    sortController = defaultSortController(),
                    onOpenRecentTickers = {
                        openRecentsClicked = true
                    },
                    onSelectTicker = { ticker ->
                        selectedTicker = ticker
                    },
                    valueFormatter = valueFormatter(),
                )
            }
        }

        composeRule.onNodeWithTag("recent_tickers_section").assertIsDisplayed()
        composeRule.onAllNodesWithTag("ticker_tile_btc").assertCountEquals(1)
        composeRule.onAllNodesWithTag("ticker_tile_eth").assertCountEquals(1)
        composeRule.onAllNodesWithTag("ticker_tile_sol").assertCountEquals(1)
        composeRule.onAllNodesWithTag("ticker_tile_ada").assertCountEquals(1)

        composeRule.onNodeWithTag("recent_tickers_title").assertHasClickAction().performClick()
        composeRule.onNodeWithTag("ticker_tile_btc").performClick()

        assertEquals(true, openRecentsClicked)
        assertEquals(bitcoin, selectedTicker)
    }

    @Test
    fun marketContainer_doesNotShowRecentSectionWhenThereAreNoRecentTickers() {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(TickersState.Success(tickers = listOf(bitcoin))),
                        sortBy = { },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _ -> Text("Market overview") },
                    sortController = defaultSortController(),
                    onOpenRecentTickers = { },
                    onSelectTicker = { },
                    valueFormatter = valueFormatter(),
                )
            }
        }

        composeRule.onAllNodesWithTag("recent_tickers_section").assertCountEquals(0)
        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
    }
}
