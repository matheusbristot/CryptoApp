package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class DropdownMenuSortCombotAction : CombotAction() {
    fun selectNameDescending() {
        onNodeWithTag("sort_name").performClick()
        onNodeWithTag("sort_desc").performClick()
    }
}

class DropdownMenuSortCombotAssert : CombotAssert() {
    fun sortOptionsAreDisplayed() {
        onNodeWithTag("sort_name").assertIsDisplayed()
        onNodeWithTag("sort_desc").assertIsDisplayed()
    }
}
