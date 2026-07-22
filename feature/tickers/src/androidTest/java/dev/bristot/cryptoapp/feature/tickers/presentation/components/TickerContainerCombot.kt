package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class TickerContainerCombotAction : CombotAction() {
    fun clickBack() {
        onNodeWithContentDescription("Back").performClick()
    }

    fun addToFavorites() {
        onNodeWithContentDescription("Add to favorites").performClick()
    }

    fun removeFromFavorites() {
        onNodeWithContentDescription("Remove from favorites").performClick()
    }
}

class TickerContainerCombotAssert : CombotAssert() {
    fun loadingContentAndBackButtonAreDisplayed(name: String) {
        onNodeWithText(name).assertIsDisplayed()
        onNodeWithContentDescription("Back").assertIsDisplayed()
        onNodeWithTag("ticker_loading").assertIsDisplayed()
    }

    fun errorIsDisplayed(name: String, message: String) {
        onNodeWithText(name).assertIsDisplayed()
        errorMessageIsDisplayed(message)
    }

    fun errorMessageIsDisplayed(message: String) {
        onNodeWithText(message).assertIsDisplayed()
    }

    fun tickerDetailsAreDisplayed() {
        onNodeWithTag("ticker_details").assertIsDisplayed()
        onNodeWithTag("ticker_price").assertIsDisplayed()
    }

    fun removeFromFavoritesIsDisplayed() {
        onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    fun removeFromFavoritesDoesNotExist() {
        onAllNodesWithContentDescription("Remove from favorites").assertCountEquals(0)
    }
}
