package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class MarketContainerCombotAction : CombotAction() {
    fun openRecentTickers() {
        onNodeWithTag("recent_tickers_title").performClick()
    }

    fun selectBitcoinTicker() {
        onNodeWithTag("ticker_tile_btc").performClick()
    }

    fun selectFavoritesTab() {
        onNodeWithTag("tickers_favorites_tab").performClick()
    }

    fun selectUnavailableFavorite(id: String) {
        onNodeWithTag("ticker_favorite_unavailable_$id").performClick()
    }
}

class MarketContainerCombotAssert : CombotAssert() {
    fun recentTickersAreDisplayed() {
        onNodeWithTag("recent_tickers_section").assertIsDisplayed()
        onAllNodesWithTag("ticker_tile_btc").assertCountEquals(1)
        onAllNodesWithTag("ticker_tile_eth").assertCountEquals(1)
        onAllNodesWithTag("ticker_tile_sol").assertCountEquals(1)
        onAllNodesWithTag("ticker_tile_ada").assertCountEquals(1)
        onNodeWithTag("recent_tickers_title").assertHasClickAction()
    }

    fun recentSectionIsHiddenAndBitcoinIsDisplayed() {
        onAllNodesWithTag("recent_tickers_section").assertCountEquals(0)
        onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
    }

    fun favoritesTabsDoNotExist() {
        onAllNodesWithTag("tickers_tab_row").assertCountEquals(0)
    }

    fun favoritesTabsAreDisplayed() {
        onNodeWithTag("tickers_tab_row").assertIsDisplayed()
        onNodeWithText("Market").assertIsDisplayed()
        onNodeWithText("Favorites").assertIsDisplayed()
    }

    fun favoritesReplaceMarketContent() {
        onNodeWithTag("ticker_favorites_list").assertIsDisplayed()
        onAllNodesWithText("Market overview").assertCountEquals(0)
    }

    fun favoritesTabsAreHiddenAndMarketIsDisplayed() {
        favoritesTabsDoNotExist()
        onNodeWithText("Market overview").assertIsDisplayed()
    }

    fun loadedAndUnavailableFavoritesAreDisplayed() {
        onNodeWithTag("ticker_tile_btc").assertIsDisplayed()
        onNodeWithTag("ticker_favorite_unavailable_missing").assertIsDisplayed()
        onNodeWithTag(
            testTag = "ticker_favorite_loading_missing",
            useUnmergedTree = true,
        ).assertIsDisplayed()
    }

    fun erroredFavoriteIsDisplayed(id: String) {
        onNodeWithTag(
            testTag = "ticker_favorite_error_$id",
            useUnmergedTree = true,
        ).assertIsDisplayed()
    }
}
