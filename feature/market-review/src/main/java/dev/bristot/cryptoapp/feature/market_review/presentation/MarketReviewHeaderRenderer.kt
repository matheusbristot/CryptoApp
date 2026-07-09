package dev.bristot.cryptoapp.feature.market_review.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Text
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewHeaderRenderer
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewComponent
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewPlaceholder
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketReviewViewModel
import dev.bristot.cryptoapp.feature.market_review.presentation.market_review.MarketViewState
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import javax.inject.Inject

class MarketReviewHeaderRenderer @Inject constructor() : MarketOverviewHeaderRenderer {

    @Composable
    override fun Render(
        isDarkMode: Boolean,
        textColors: AppTextColors,
    ) {
        val viewModel = hiltViewModel<MarketReviewViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        when (val stateValue = state.value) {
            MarketViewState.Initial,
            MarketViewState.Loading -> MarketReviewPlaceholder(isDarkMode = isDarkMode)

            is MarketViewState.MarketReviewData -> MarketReviewComponent(
                isDarkMode = isDarkMode,
                textColor = textColors.primary,
                secondaryTextColor = textColors.secondary,
                stats = stateValue.data,
            )

            is MarketViewState.Error -> Text(
                text = stateValue.message,
                color = Color.Red,
            )
        }
    }
}
