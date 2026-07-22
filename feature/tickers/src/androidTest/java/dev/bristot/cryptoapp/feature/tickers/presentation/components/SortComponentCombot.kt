package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class SortComponentCombotAction : CombotAction() {
    fun selectNameSort() {
        openSortMenu()
        onNodeWithTag("sort_name").performClick()
    }

    fun selectRankSort() {
        openSortMenu()
        onNodeWithTag("sort_rank").performClick()
    }

    private fun openSortMenu() {
        onNodeWithContentDescription("Change sort").performClick()
    }
}

class SortComponentCombotAssert : CombotAssert() {
    fun sortMenuIsClosed() {
        onAllNodesWithTag("dropdown_sort_menu").assertCountEquals(0)
    }
}
