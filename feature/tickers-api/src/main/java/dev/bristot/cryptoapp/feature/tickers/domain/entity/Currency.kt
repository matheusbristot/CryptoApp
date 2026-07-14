package dev.bristot.cryptoapp.feature.tickers.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val price: Double,
    val volume24h: Double,
    val volume24hChange24h: Double,
    val marketCap: MarketCap,
    val percentChangeInterval: PercentChangeInterval,
    val allTimeHigh: AllTimeHigh,
)
