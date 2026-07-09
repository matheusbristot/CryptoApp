package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.RecentTickersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecentTickersViewModel @Inject constructor(
    private val recentTickersRepository: RecentTickersRepository,
) : ViewModel() {

    val state: StateFlow<RecentTickersState> = recentTickersRepository
        .observeRecentTickers()
        .map { tickers -> RecentTickersState(tickers = tickers) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = RecentTickersState(),
        )

    fun addRecentTicker(ticker: Ticker) {
        recentTickersRepository.addRecentTicker(ticker)
    }
}
