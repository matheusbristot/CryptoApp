package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortOrder
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortType

internal fun sortTickers(
    tickers: List<Ticker>,
    sortType: SortType,
    sortOrder: SortOrder,
): List<Ticker> {
    return when (sortType) {
        SortType.RANK -> {
            val selector: (Ticker) -> Int? = { ticker -> ticker.rank }
            if (sortOrder == SortOrder.ASCENDING) {
                tickers.sortedBy(selector)
            } else {
                tickers.sortedByDescending(selector)
            }
        }

        SortType.NAME -> {
            val selector: (Ticker) -> String? = { ticker -> ticker.name }
            if (sortOrder == SortOrder.ASCENDING) {
                tickers.sortedBy(selector)
            } else {
                tickers.sortedByDescending(selector)
            }
        }

        SortType.SYMBOL -> {
            val selector: (Ticker) -> String? = { ticker -> ticker.symbol }
            if (sortOrder == SortOrder.ASCENDING) {
                tickers.sortedBy(selector)
            } else {
                tickers.sortedByDescending(selector)
            }
        }
    }
}
