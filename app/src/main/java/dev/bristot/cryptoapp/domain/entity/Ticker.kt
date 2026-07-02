package dev.bristot.cryptoapp.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Ticker(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val prices: Map<CurrencySymbol, Currency>
)
