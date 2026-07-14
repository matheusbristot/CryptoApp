package dev.bristot.cryptoapp.feature.market_review.api

import androidx.compose.runtime.Composable
import dev.bristot.cryptoapp.ui.theme.AppTextColors

interface MarketOverviewHeaderRenderer {
    @Composable
    fun Render(
        isDarkMode: Boolean,
        textColors: AppTextColors,
        quoteData: MarketOverviewQuoteData?,
    )
}
