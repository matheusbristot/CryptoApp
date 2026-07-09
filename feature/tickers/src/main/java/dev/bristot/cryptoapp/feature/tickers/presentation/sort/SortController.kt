package dev.bristot.cryptoapp.feature.tickers.presentation.sort

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
class SortController(
    val state: StateFlow<SortState>,
    val changeType: (sortType: SortType) -> Unit,
    val changeOrder: (sortOrder: SortOrder) -> Unit,
)
