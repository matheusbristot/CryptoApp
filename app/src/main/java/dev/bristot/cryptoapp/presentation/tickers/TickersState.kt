package dev.bristot.cryptoapp.presentation.tickers

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.domain.entity.Ticker

@Immutable
sealed class TickersState {
    object Initial : TickersState()

    data class Success(val tickers: List<Ticker>) : TickersState()

    data class Error(val error: String) : TickersState()

    object Loading : TickersState()
}
