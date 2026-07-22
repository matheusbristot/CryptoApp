package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class CoinListLoadingCombotAssert : CombotAssert() {
    fun loadingIndicatorIsDisplayed() {
        onNodeWithTag("coin_list_loading").assertIsDisplayed()
    }
}
