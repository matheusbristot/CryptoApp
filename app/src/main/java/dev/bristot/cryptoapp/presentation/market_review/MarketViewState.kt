package dev.bristot.cryptoapp.presentation.market_review

import androidx.compose.runtime.Immutable

@Immutable
sealed class MarketViewState {

    object Initial : MarketViewState()

    object Loading : MarketViewState()

    data class MarketReviewData(val data: List<MarketStats>) : MarketViewState()

    data class Error(val message: String) : MarketViewState()
}
