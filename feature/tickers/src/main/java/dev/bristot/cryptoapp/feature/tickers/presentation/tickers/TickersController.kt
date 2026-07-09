package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortOrder
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortType
import kotlinx.coroutines.flow.StateFlow

@Stable
data class TickersController(
    val state: StateFlow<TickersState>,
    val sortBy: (sortType: SortType, sortOrder: SortOrder) -> Unit,
)
