package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class MoveToFirstTileFloatingButtonCombotAction : CombotAction() {
    fun clickButton() {
        onNodeWithTag("move_to_first_tile_button").performClick()
    }
}

class MoveToFirstTileFloatingButtonCombotAssert : CombotAssert() {
    fun buttonIsClickable() {
        onNodeWithTag("move_to_first_tile_button").assertHasClickAction()
    }
}
