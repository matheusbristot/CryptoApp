package dev.bristot.cryptoapp.presentation.recents

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.domain.entity.Ticker

@Immutable
data class RecentTickersState(
    val tickers: List<Ticker> = emptyList(),
)
