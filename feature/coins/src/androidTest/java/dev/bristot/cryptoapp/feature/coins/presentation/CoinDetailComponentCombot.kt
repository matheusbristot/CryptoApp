package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class CoinDetailComponentCombotAction : CombotAction() {
    fun clickFavorite() {
        onNodeWithTag("coin_favorite_button").performClick()
    }
}

class CoinDetailComponentCombotAssert : CombotAssert() {
    fun errorIsDisplayed() {
        onNodeWithTag("coin_detail_error").assertIsDisplayed()
    }
}
