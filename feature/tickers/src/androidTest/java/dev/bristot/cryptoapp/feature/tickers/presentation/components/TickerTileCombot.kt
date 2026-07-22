package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.gabrielbrasileiro.combot.core.CombotAction
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class TickerTileCombotAction : CombotAction() {
    fun clickBitcoinTicker() {
        onNodeWithTag("ticker_tile_btc").performClick()
    }
}

class TickerTileCombotAssert : CombotAssert() {
    fun tickerInfoIsDisplayed() {
        onNodeWithTag("ticker_tile_btc").assertIsDisplayed().assertHasClickAction()
        onNodeWithTag("ticker_price", useUnmergedTree = true).assertIsDisplayed()
        onNodeWithText("+0.60%").assertIsDisplayed()
        onNodeWithText("24h").assertIsDisplayed()
    }
}
