package dev.bristot.cryptoapp.feature.tickers.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class MarketCap(
    val marketCap: Double,
    val lastChangeTwentyFourHours: Double,
)
