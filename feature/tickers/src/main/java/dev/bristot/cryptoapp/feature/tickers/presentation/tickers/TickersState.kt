package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.Immutable
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker

@Immutable
sealed class TickersState {
    object Initial : TickersState()
    data class Success(val tickers: List<Ticker>) : TickersState()
    data class Error(val error: String) : TickersState()
    object Loading : TickersState()
}

enum class TickersSection {
    MARKET,
    FAVORITES,
}

@Immutable
data class FavoriteTickerState(
    val ref: FavoriteRef,
    val ticker: Ticker? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@Immutable
data class TickerFavoritesState(
    val items: List<FavoriteTickerState> = emptyList(),
) {
    val count: Int get() = items.size
    val tickers: List<Ticker> get() = items.mapNotNull(FavoriteTickerState::ticker)
}
