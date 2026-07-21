package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListSection
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.FavoriteCoinItem
import dev.bristot.cryptoapp.ui.sort.SortState
import kotlinx.coroutines.flow.StateFlow

@Stable
data class CoinListController(
    val state: StateFlow<CoinListState>,
    val favorites: StateFlow<List<FavoriteCoinItem>>,
    val selectedSection: StateFlow<CoinListSection>,
    val refreshIfNeeded: () -> Unit,
    val setActive: (Boolean) -> Unit,
    val handleToTop: (Boolean) -> Unit,
    val sortBy: (SortState) -> Unit,
    val selectSection: (CoinListSection) -> Unit,
)
