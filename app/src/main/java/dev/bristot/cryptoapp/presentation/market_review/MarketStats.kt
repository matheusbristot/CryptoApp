package dev.bristot.cryptoapp.presentation.market_review

import kotlinx.serialization.Serializable

@Serializable
data class MarketStats(
    val label: String, val value: String, val change: String, val isPositive: Boolean,
)