package dev.bristot.cryptoapp.presentation.ticker

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.domain.entity.Ticker

@Immutable
sealed class TickerState {
    object Initial : TickerState()

    data class Success(val ticker: Ticker) : TickerState()

    data class Error(val error: String) : TickerState()

    object Loading : TickerState()
}
