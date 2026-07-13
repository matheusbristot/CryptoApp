package dev.bristot.cryptoapp.feature.tickers.presentation.components

import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.sort.SortOrder
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortType
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ListState
import dev.bristot.cryptoapp.ui.widgets.floating_button.ScrollStateSavable
import dev.bristot.cryptoapp.format.DefaultCryptoValueFormatter
import kotlinx.coroutines.flow.MutableStateFlow

internal fun valueFormatter() = DefaultCryptoValueFormatter()

internal fun ticker(
    id: String = "btc",
    name: String = "Bitcoin",
    symbol: String = "BTC",
    rank: Int = 1,
) = Ticker(
    id = id,
    name = name,
    symbol = symbol,
    rank = rank,
    totalSupply = 1_000L,
    maxSupply = 2_000L,
    betaValue = 1.0,
    firstDataAt = "2026-01-01T00:00:00Z",
    lastUpdated = "2026-07-01T00:00:00Z",
    prices = mapOf(
        CurrencySymbol.BRL to Currency(
            price = 71_420.0,
            volume24h = 100.0,
            volume24hChange24h = 1.5,
            marketCap = MarketCap(
                marketCap = 1_000.0,
                lastChangeTwentyFourHours = 2.5,
            ),
            percentChangeInterval = PercentChangeInterval(
                p15m = 0.1,
                p30m = 0.2,
                p1h = 0.3,
                p6h = 0.4,
                p12h = 0.5,
                p24h = 0.6,
                p7d = 0.7,
                p30d = 0.8,
                p1y = 0.9,
            ),
            allTimeHigh = AllTimeHigh(
                price = 3_000.0,
                date = "2026-01-01T00:00:00Z",
                percentage = -10.0,
            ),
        )
    ),
)

internal fun hiddenFloatingButtonController() = FloatingButtonController(
    state = MutableStateFlow(
        ScrollStateSavable(
            floatingButtonState = FloatingButtonState.Hidden,
            listState = ListState(),
        )
    ),
    onHandleVisibility = { },
    onSaveScroll = { _, _ -> },
)

internal fun defaultSortController() = SortController(
    state = MutableStateFlow(
        SortState(
            type = SortType.RANK,
            order = SortOrder.ASCENDING,
        )
    ),
    changeType = { },
    changeOrder = { },
)
