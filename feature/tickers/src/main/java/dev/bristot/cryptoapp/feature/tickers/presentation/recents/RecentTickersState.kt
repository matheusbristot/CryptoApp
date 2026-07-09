package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker

@Immutable
data class RecentTickersState(
    val tickers: List<Ticker> = emptyList(),
)
