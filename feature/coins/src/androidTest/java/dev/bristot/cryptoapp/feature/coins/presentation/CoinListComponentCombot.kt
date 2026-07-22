package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class CoinListComponentCombotAssert : CombotAssert() {
    fun favoritesTabDoesNotExist() {
        onAllNodesWithTag("coin_tab_favorites").assertCountEquals(0)
    }

    fun allAndFavoritesTabsAreDisplayed() {
        onNodeWithTag("coin_tab_all").assertIsDisplayed()
        onNodeWithTag("coin_tab_favorites").assertIsDisplayed()
        onNodeWithText("All").assertIsDisplayed()
        onNodeWithText("Favorites").assertIsDisplayed()
    }
}
