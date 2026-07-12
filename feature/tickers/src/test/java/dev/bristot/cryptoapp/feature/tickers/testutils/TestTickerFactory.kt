package dev.bristot.cryptoapp.feature.tickers.testutils

import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker

fun testTicker(
    id: String,
    name: String,
    symbol: String,
    rank: Int,
    price: Double = 1.0,
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
            price = price,
            volume24h = 1.0,
            volume24hChange24h = 1.0,
            marketCap = MarketCap(
                marketCap = 1.0,
                lastChangeTwentyFourHours = 1.0,
            ),
            percentChangeInterval = PercentChangeInterval(
                p15m = 1.0,
                p30m = 1.0,
                p1h = 1.0,
                p6h = 1.0,
                p12h = 1.0,
                p24h = 1.0,
                p7d = 1.0,
                p30d = 1.0,
                p1y = 1.0,
            ),
            allTimeHigh = AllTimeHigh(
                price = 1.0,
                date = "2026-07-01",
                percentage = 1.0,
            ),
        ),
    ),
)
