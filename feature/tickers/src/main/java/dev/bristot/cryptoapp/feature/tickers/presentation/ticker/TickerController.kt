package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import kotlinx.coroutines.flow.StateFlow

@Stable
data class TickerController(
    val state: StateFlow<TickerState>,
    val quoteCurrency: StateFlow<QuoteCurrency>,
    val refreshIfNeeded: () -> Unit,
)
