package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import br.com.gabrielbrasileiro.combot.core.CombotAssert

class MarketReviewComponentCombotAssert : CombotAssert() {
    fun marketStatsAreDisplayed() {
        onNodeWithTag("market_review_component").assertIsDisplayed()
        onNodeWithTag("market_review_header").assertIsDisplayed()
        onNodeWithTag("market_review_live_badge").assertIsDisplayed()
        onNodeWithTag("market_review_stats").assertIsDisplayed()
        onNodeWithTag("market_stat_total_market_cap").assertIsDisplayed()
        onNodeWithTag("market_stat_24h_volume").assertIsDisplayed()
        onNodeWithText("Global Market").assertIsDisplayed()
        onNodeWithText("Live").assertIsDisplayed()
        onNodeWithText("\$1,5M").assertIsDisplayed()
        onNodeWithText("\$250000").assertIsDisplayed()
        onNodeWithText("Market overview · BRL").assertIsDisplayed()
        onNodeWithContentDescription("Positive 24-hour change")
            .assertContentDescriptionEquals("Positive 24-hour change")
        onNodeWithContentDescription("Negative 24-hour change")
            .assertContentDescriptionEquals("Negative 24-hour change")
    }

    fun headerIsDisplayedWithoutStats() {
        onNodeWithTag("market_review_component").assertIsDisplayed()
        onNodeWithTag("market_review_header").assertIsDisplayed()
        onNodeWithTag("market_review_stats").assertDoesNotExist()
    }

    fun placeholderIsDisplayed() {
        onNodeWithTag("market_review_placeholder").assertIsDisplayed()
    }
}
