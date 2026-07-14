package dev.bristot.cryptoapp.feature.tickers.data.dto

import dev.bristot.cryptoapp.feature.tickers.data.model.CurrencyResponse
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker

fun TickerResponse.toTicker(currenciesOf: Set<CurrencySymbol>): Ticker {
    return currenciesOf.mapNotNull { symbol ->
        val quote = quotes[symbol.name] ?: return@mapNotNull null
        val percentage = buildPercentage(quote = quote)
        val allTimeHigh = buildAllTimeHigh(quote = quote)
        val marketCap = buildMarketCap(quote = quote)
        val currency = Currency(
            price = quote.price,
            volume24h = quote.volume24h,
            volume24hChange24h = quote.volume24hChange24h,
            marketCap = marketCap,
            percentChangeInterval = percentage,
            allTimeHigh = allTimeHigh
        )
        symbol to currency
    }.toMap().let { prices ->
        Ticker(
            id = id,
            name = name,
            symbol = symbol,
            rank = rank,
            totalSupply = totalSupply,
            maxSupply = maxSupply,
            betaValue = betaValue,
            firstDataAt = firstDataAt,
            lastUpdated = lastUpdated,
            prices = prices
        )
    }
}

fun List<TickerResponse>.toTickers(currenciesOf: Set<CurrencySymbol>): List<Ticker> =
    map { ticker ->
        ticker.toTicker(currenciesOf = currenciesOf)
    }

private fun buildMarketCap(quote: CurrencyResponse) = with(receiver = quote) {
    return@with MarketCap(
        marketCap = quote.marketCap, lastChangeTwentyFourHours = quote.marketCapChange24h
    )
}

private fun buildAllTimeHigh(quote: CurrencyResponse) = with(receiver = quote) {
    return@with AllTimeHigh(
        price = quote.athPrice, date = quote.athDate, percentage = quote.percentFromPriceAth
    )
}

private fun buildPercentage(quote: CurrencyResponse): PercentChangeInterval =
    with(receiver = quote) {
        return@with PercentChangeInterval(
            p15m = percentChange15m,
            p30m = percentChange30m,
            p1h = percentChange1h,
            p6h = percentChange6h,
            p12h = percentChange12h,
            p24h = percentChange24h,
            p7d = percentChange7d,
            p30d = percentChange30d,
            p1y = percentChange1y,
        )
    }
