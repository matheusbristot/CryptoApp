package dev.bristot.cryptoapp.presentation.market_review

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
class MarketReviewController(
    val state: StateFlow<MarketViewState>,
)
