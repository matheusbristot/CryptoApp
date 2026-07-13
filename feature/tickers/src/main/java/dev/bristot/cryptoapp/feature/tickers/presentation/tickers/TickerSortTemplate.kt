package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import javax.inject.Inject

class TickerSortTemplate @Inject constructor() : SortTemplate<Ticker>() {
    override fun rankOf(item: Ticker): Int = item.rank
    override fun nameOf(item: Ticker): String = item.name
    override fun symbolOf(item: Ticker): String = item.symbol
}
