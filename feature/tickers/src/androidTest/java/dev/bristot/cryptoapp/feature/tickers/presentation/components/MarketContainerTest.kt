package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersState
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.MarketContainer
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickerFavoritesState
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.FavoriteTickerState
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersSection
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickersState
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewQuoteData
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
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
        var overviewQuoteData: MarketOverviewQuoteData? = null

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(
                            TickersState.Success(
                                tickers = listOf(bitcoin, ethereum, solana, cardano, xrp),
                            )
                        ),
                        quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
                        refreshIfNeeded = { },
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
                    marketOverviewHeaderContent = { _, _, quoteData ->
                        overviewQuoteData = quoteData
                        Text("Market overview")
                    },
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
        assertEquals("BRL", overviewQuoteData?.currencyCode)
        assertEquals(5_000.0, overviewQuoteData?.marketCap)
        assertEquals(500.0, overviewQuoteData?.volume24h)
    }

    @Test
    fun marketContainer_doesNotShowRecentSectionWhenThereAreNoRecentTickers() {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(TickersState.Success(tickers = listOf(bitcoin))),
                        quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
                        refreshIfNeeded = { },
                        sortBy = { },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _, _ -> Text("Market overview") },
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

    @Test
    fun favoritesTabs_areHiddenAtZeroAndFavoriteContentReplacesMarket() {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val favoritesState = MutableStateFlow(TickerFavoritesState())
        val selectedSection = MutableStateFlow(TickersSection.MARKET)

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(TickersState.Success(tickers = listOf(bitcoin))),
                        quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
                        refreshIfNeeded = { },
                        sortBy = { },
                        favoritesState = favoritesState,
                        selectedSection = selectedSection,
                        selectSection = { selectedSection.value = it },
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _, _ -> Text("Market overview") },
                    sortController = defaultSortController(),
                    onOpenRecentTickers = { },
                    onSelectTicker = { },
                    valueFormatter = valueFormatter(),
                )
            }
        }

        composeRule.onAllNodesWithTag("tickers_tab_row").assertCountEquals(0)
        composeRule.runOnIdle {
            favoritesState.value = TickerFavoritesState(
                items = listOf(
                    FavoriteTickerState(
                        ref = FavoriteRef(FavoriteType.TICKER, bitcoin.id, 1L),
                        ticker = bitcoin,
                    ),
                ),
            )
        }
        composeRule.onNodeWithTag("tickers_tab_row").assertIsDisplayed()
        composeRule.onNodeWithTag("tickers_favorites_tab").performClick()
        composeRule.onNodeWithTag("ticker_favorites_list").assertIsDisplayed()
        composeRule.onAllNodesWithText("Market overview").assertCountEquals(0)

        composeRule.runOnIdle {
            selectedSection.value = TickersSection.MARKET
            favoritesState.value = TickerFavoritesState()
        }
        composeRule.onAllNodesWithTag("tickers_tab_row").assertCountEquals(0)
        composeRule.onNodeWithText("Market overview").assertIsDisplayed()
    }

    @Test
    fun favoritesContent_keepsUnavailableItemVisibleBesideLoadedItem() {
        val bitcoin = ticker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        var selectedFavorite: Pair<String, String>? = null
        val favoritesState = MutableStateFlow(
            TickerFavoritesState(
                items = listOf(
                    FavoriteTickerState(
                        ref = FavoriteRef(FavoriteType.TICKER, bitcoin.id, 2L),
                        ticker = bitcoin,
                    ),
                    FavoriteTickerState(
                        ref = FavoriteRef(FavoriteType.TICKER, "missing", 1L),
                        isLoading = true,
                    ),
                ),
            ),
        )

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(TickersState.Success(emptyList())),
                        quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
                        refreshIfNeeded = { },
                        sortBy = { },
                        favoritesState = favoritesState,
                        selectedSection = MutableStateFlow(TickersSection.FAVORITES),
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _, _ -> Text("Market overview") },
                    sortController = defaultSortController(),
                    onOpenRecentTickers = { },
                    onSelectTicker = { },
                    onSelectFavorite = { id, name -> selectedFavorite = id to name },
                    valueFormatter = valueFormatter(),
                )
            }
        }

        composeRule.onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
        composeRule.onNodeWithTag("ticker_favorite_unavailable_missing").assertIsDisplayed()
        composeRule.onNodeWithTag(
            testTag = "ticker_favorite_loading_missing",
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("ticker_favorite_unavailable_missing").performClick()

        assertEquals("missing" to "missing", selectedFavorite)
    }

    @Test
    fun favoritesContent_keepsSingleErroredItemVisibleAndClickable() {
        var selectedId: String? = null
        val unavailable = FavoriteTickerState(
            ref = FavoriteRef(FavoriteType.TICKER, "offline", 1L),
            error = "Sem conexão",
        )

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                MarketContainer(
                    tickersController = TickersController(
                        state = MutableStateFlow(TickersState.Success(emptyList())),
                        quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
                        refreshIfNeeded = { },
                        sortBy = { },
                        favoritesState = MutableStateFlow(TickerFavoritesState(listOf(unavailable))),
                        selectedSection = MutableStateFlow(TickersSection.FAVORITES),
                    ),
                    recentTickersController = RecentTickersController(
                        state = MutableStateFlow(RecentTickersState()),
                        addRecentTicker = { },
                    ),
                    floatingButtonController = hiddenFloatingButtonController(),
                    marketOverviewHeaderContent = { _, _, _ -> Text("Market overview") },
                    sortController = defaultSortController(),
                    onOpenRecentTickers = { },
                    onSelectTicker = { },
                    onSelectFavorite = { id, _ -> selectedId = id },
                    valueFormatter = valueFormatter(),
                )
            }
        }

        composeRule.onNodeWithTag(
            testTag = "ticker_favorite_error_offline",
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("ticker_favorite_unavailable_offline").performClick()
        assertEquals("offline", selectedId)
    }
}
