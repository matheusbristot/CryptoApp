package dev.bristot.cryptoapp.presentation.recents

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.domain.entity.Ticker
import kotlinx.coroutines.flow.StateFlow

@Stable
data class RecentTickersController(
    val state: StateFlow<RecentTickersState>,
    val addRecentTicker: (Ticker) -> Unit,
)
