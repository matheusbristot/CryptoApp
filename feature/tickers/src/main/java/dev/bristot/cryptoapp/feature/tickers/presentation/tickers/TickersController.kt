package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.ui.sort.SortState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class TickersController(
    val state: StateFlow<TickersState>,
    val quoteCurrency: StateFlow<QuoteCurrency>,
    val refreshIfNeeded: () -> Unit,
    val sortBy: (SortState) -> Unit,
    val favoritesState: StateFlow<TickerFavoritesState> = MutableStateFlow(TickerFavoritesState()),
    val selectedSection: StateFlow<TickersSection> = MutableStateFlow(TickersSection.MARKET),
    val selectSection: (TickersSection) -> Unit = {},
    val setActive: (Boolean) -> Unit = {},
)
