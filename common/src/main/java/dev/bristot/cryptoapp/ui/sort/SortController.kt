package dev.bristot.cryptoapp.ui.sort

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
class SortController(
    val state: StateFlow<SortState>,
    val changeType: (SortType) -> Unit,
    val changeOrder: (SortOrder) -> Unit,
)
