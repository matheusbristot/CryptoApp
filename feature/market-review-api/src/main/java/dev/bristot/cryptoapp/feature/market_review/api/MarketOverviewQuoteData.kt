package dev.bristot.cryptoapp.feature.market_review.api

data class MarketOverviewQuoteData(
    val currencyCode: String,
    val marketCap: Double,
    val volume24h: Double,
)
