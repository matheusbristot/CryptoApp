package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
data class TickerController(
    val state: StateFlow<TickerState>,
    val onLoadContent: () -> Unit,
)
