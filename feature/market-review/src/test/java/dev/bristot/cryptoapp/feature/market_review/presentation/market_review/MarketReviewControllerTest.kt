package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertSame
import org.junit.Test

class MarketReviewControllerTest {

    @Test
    fun marketReviewController_keepsStateReference() {
        val state = MutableStateFlow<MarketViewState>(MarketViewState.Initial)

        val controller = MarketReviewController(state = state)

        assertSame(state, controller.state)
    }
}
