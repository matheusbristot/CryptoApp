package dev.bristot.cryptoapp.feature.market_review.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Text
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewHeaderRenderer
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewQuoteData
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewComponent
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewPlaceholder
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewViewModel
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketViewState
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import javax.inject.Inject

class MarketReviewHeaderRenderer @Inject constructor(
    private val valueFormatter: CryptoValueFormatter,
) : MarketOverviewHeaderRenderer {

    @Composable
    override fun Render(
        isDarkMode: Boolean,
        textColors: AppTextColors,
        quoteData: MarketOverviewQuoteData?,
    ) {
        val viewModel = hiltViewModel<MarketReviewViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        when (val stateValue = state.value) {
            MarketViewState.Initial,
            MarketViewState.Loading -> MarketReviewPlaceholder(isDarkMode = isDarkMode)

            is MarketViewState.MarketReviewData -> if (quoteData == null) {
                MarketReviewPlaceholder(isDarkMode = isDarkMode)
            } else {
                MarketReviewComponent(
                    isDarkMode = isDarkMode,
                    textColor = textColors.primary,
                    secondaryTextColor = textColors.secondary,
                    quoteCurrency = quoteData.currencyCode,
                    stats = stateValue.data.mapIndexed { index, stats ->
                        val value = if (index == 0) quoteData.marketCap else quoteData.volume24h
                        stats.copy(
                            value = valueFormatter.compactCurrency(
                                value = value,
                                currencyCode = quoteData.currencyCode,
                            )
                        )
                    },
                )
            }

            is MarketViewState.Error -> Text(
                text = stateValue.message,
                color = Color.Red,
            )
        }
    }
}
