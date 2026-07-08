package dev.bristot.cryptoapp.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
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
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewController
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketViewState
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinListLoading
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinListTile
import dev.bristot.cryptoapp.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.presentation.recents.RecentTickersState
import dev.bristot.cryptoapp.presentation.ticker.TickerContainer
import dev.bristot.cryptoapp.presentation.ticker.TickerController
import dev.bristot.cryptoapp.presentation.ticker.TickerState
import dev.bristot.cryptoapp.presentation.tickers.MarketContainer
import dev.bristot.cryptoapp.presentation.tickers.TickersController
import dev.bristot.cryptoapp.presentation.tickers.TickersState
import dev.bristot.cryptoapp.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ListState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ScrollStateSavable
import dev.bristot.cryptoapp.ui.widgets.sort.DropdownMenuSort
import dev.bristot.cryptoapp.ui.widgets.sort.SortController
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortState
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
                        sortBy = { _, _ -> },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(
                            RecentTickersState(
                                tickers = listOf(bitcoin, ethereum, solana, cardano),
                            )
                        ),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = FloatingButtonController(
                        state = MutableStateFlow(
                            ScrollStateSavable(
                                floatingButtonState = FloatingButtonState.Hidden,
                                listState = ListState(),
                            )
                        ),
                        onHandleVisibility = { },
                        onSaveScroll = { _, _ -> },
                    ),
                    marketReviewController = MarketReviewController(
                        state = MutableStateFlow(MarketViewState.MarketReviewData(emptyList())),
                    ),
                    sortController = SortController(
                        state = MutableStateFlow(
                            SortState(
                                type = SortType.RANK,
                                order = SortOrder.ASCENDING,
                            )
                        ),
                        changeType = { },
                        changeOrder = { },
                    ),
                    onOpenRecentTickers = {
                        openRecentsClicked = true
                    },
                    onSelectTicker = { ticker ->
                        selectedTicker = ticker
                    },
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
                        sortBy = { _, _ -> },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = FloatingButtonController(
                        state = MutableStateFlow(
                            ScrollStateSavable(
                                floatingButtonState = FloatingButtonState.Hidden,
                                listState = ListState(),
                            )
                        ),
                        onHandleVisibility = { },
                        onSaveScroll = { _, _ -> },
                    ),
                    marketReviewController = MarketReviewController(
                        state = MutableStateFlow(MarketViewState.MarketReviewData(emptyList())),
                    ),
                    sortController = SortController(
                        state = MutableStateFlow(
                            SortState(
                                type = SortType.RANK,
                                order = SortOrder.ASCENDING,
                            )
                        ),
                        changeType = { },
                        changeOrder = { },
                    ),
                    onOpenRecentTickers = { },
                    onSelectTicker = { },
                )
            }
        }

        composeRule.onNodeWithTag("recent_tickers_section").assertDoesNotExist()
        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
    }

    private fun ticker(
        id: String = "btc",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        rank: Int = 1,
    ) = Ticker(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
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
