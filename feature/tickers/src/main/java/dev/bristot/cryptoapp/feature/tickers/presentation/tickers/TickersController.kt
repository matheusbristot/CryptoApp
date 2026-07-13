package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.ui.sort.SortState
import kotlinx.coroutines.flow.StateFlow

@Stable
data class TickersController(
    val state: StateFlow<TickersState>,
    val sortBy: (SortState) -> Unit,
)
