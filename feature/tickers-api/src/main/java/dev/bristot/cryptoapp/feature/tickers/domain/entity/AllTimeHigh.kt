package dev.bristot.cryptoapp.feature.tickers.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AllTimeHigh(
    val price: Double?,
    val date: String?,
    val percentage: Double?,
)
