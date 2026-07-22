package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class CoinListTileCombotAssert : CombotAssert() {
    fun bitcoinInfoIsDisplayed() {
        onNodeWithTag("coin_tile_btc").assertIsDisplayed()
        onNodeWithText("Bitcoin").assertIsDisplayed()
        onNodeWithText("BTC", substring = true).assertIsDisplayed()
        onNodeWithText("B").assertIsDisplayed()
    }
}
