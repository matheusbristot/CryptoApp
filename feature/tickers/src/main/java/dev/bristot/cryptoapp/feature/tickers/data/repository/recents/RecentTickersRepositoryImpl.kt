package dev.bristot.cryptoapp.feature.tickers.data.repository.recents

import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.RecentTickersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ActivityRetainedScoped
class RecentTickersRepositoryImpl @Inject constructor() : RecentTickersRepository {

    private val recentTickers = MutableStateFlow<List<Ticker>>(emptyList())

    override fun observeRecentTickers(): StateFlow<List<Ticker>> = recentTickers

    override fun addRecentTicker(ticker: Ticker) {
        recentTickers.update { currentTickers ->
            listOf(ticker) + currentTickers.filterNot { currentTicker ->
                currentTicker.id == ticker.id
            }
        }
    }
}
