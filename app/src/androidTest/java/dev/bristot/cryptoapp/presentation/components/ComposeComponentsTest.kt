package dev.bristot.cryptoapp.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import dev.bristot.cryptoapp.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.domain.entity.Coin
import dev.bristot.cryptoapp.domain.entity.Currency
import dev.bristot.cryptoapp.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.domain.entity.MarketCap
import dev.bristot.cryptoapp.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinListLoading
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinListTile
import dev.bristot.cryptoapp.presentation.market_review.MarketReviewComponent
import dev.bristot.cryptoapp.presentation.market_review.MarketStats
import dev.bristot.cryptoapp.presentation.ticker.TickerContainer
import dev.bristot.cryptoapp.presentation.ticker.TickerController
import dev.bristot.cryptoapp.presentation.ticker.TickerState
import dev.bristot.cryptoapp.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import dev.bristot.cryptoapp.ui.widgets.sort.DropdownMenuSort
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortType
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
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
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
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
                    onClick = { id, name ->
                        clickedId = id
                        clickedName = name
                    },
                )
            }
        }

        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed().assertHasClickAction().performClick()
        composeRule.onNodeWithText("71420.0").assertIsDisplayed()
        composeRule.onNodeWithText("1.5%").assertIsDisplayed()

        assertEquals("btc", clickedId)
        assertEquals("Bitcoin", clickedName)
    }

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

    @Test
    fun moveToFirstTileFloatingButton_isClickable() {
        var clicked = false

        composeRule.setContent {
            MoveToFirstTileFloatingButton(onClick = { clicked = true })
        }

        composeRule.onNodeWithTag("move_to_first_tile_button").assertHasClickAction().performClick()

        assertEquals(true, clicked)
    }

    @Test
    fun tickerContainer_showsLoadingCallsLoadAndBackButton() {
        var onLoadCalled = false
        var backClicked = false
        val state = MutableStateFlow<TickerState>(TickerState.Loading)
        val controller = TickerController(
            state = state,
            onLoadContent = { onLoadCalled = true }
        )

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Bitcoin",
                    tickerController = controller,
                    onBackButtonClick = { backClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("ticker_loading").assertIsDisplayed()
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()

        assertEquals(true, onLoadCalled)
        assertEquals(true, backClicked)
    }

    @Test
    fun tickerContainer_showsErrorAndSuccessContent() {
        val loadingState = MutableStateFlow<TickerState>(TickerState.Error("An error occurred"))
        val controller = TickerController(
            state = loadingState,
            onLoadContent = { }
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Ethereum",
                    tickerController = controller,
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

    @Test
    fun dropdownMenuSort_invokesCallbacksForTypeAndOrder() {
        var receivedType: SortType? = null
        var receivedOrder: SortOrder? = null

        composeRule.setContent {
            DropdownMenuSort(
                expanded = true,
                onChangeSortType = { receivedType = it },
                onChangeSortOrder = { receivedOrder = it },
                onDismiss = { },
            )
        }

        composeRule.onNodeWithTag("sort_name").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("sort_desc").assertIsDisplayed().performClick()

        assertEquals(SortType.NAME, receivedType)
        assertEquals(SortOrder.DESCENDING, receivedOrder)
    }

    private fun ticker() = Ticker(
        id = "btc",
        name = "Bitcoin",
        symbol = "BTC",
        rank = 1,
        prices = mapOf(
            CurrencySymbol.BRL to Currency(
                price = 71_420.0,
                volume24h = 100.0,
                volume24hChange24h = 1.5,
                marketCap = MarketCap(
                    marketCap = 1_000.0,
                    lastChangeTwentyFourHours = 2.5,
                ),
                percentChangeInterval = PercentChangeInterval(
                    p15m = 0.1,
                    p30m = 0.2,
                    p1h = 0.3,
                    p6h = 0.4,
                    p12h = 0.5,
                    p24h = 0.6,
                    p7d = 0.7,
                    p30d = 0.8,
                    p1y = 0.9,
                ),
                allTimeHigh = AllTimeHigh(
                    price = 3_000.0,
                    date = "2026-01-01T00:00:00Z",
                    percentage = -10.0,
                ),
            )
        ),
    )
}
