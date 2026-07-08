package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import kotlinx.serialization.Serializable

@Serializable
data class MarketStats(
    val label: String, val value: String, val change: String, val isPositive: Boolean,
)
