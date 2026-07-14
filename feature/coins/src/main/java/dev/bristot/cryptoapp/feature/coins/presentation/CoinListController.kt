package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.ui.sort.SortState
import kotlinx.coroutines.flow.StateFlow

@Stable
data class CoinListController(
    val state: StateFlow<CoinListState>,
    val refreshIfNeeded: () -> Unit,
    val handleToTop: (Boolean) -> Unit,
    val sortBy: (SortState) -> Unit,
)
