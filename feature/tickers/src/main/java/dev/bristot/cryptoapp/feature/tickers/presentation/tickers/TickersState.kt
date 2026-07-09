package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker

@Immutable
sealed class TickersState {
    object Initial : TickersState()

    data class Success(val tickers: List<Ticker>) : TickersState()

    data class Error(val error: String) : TickersState()

    object Loading : TickersState()
}
