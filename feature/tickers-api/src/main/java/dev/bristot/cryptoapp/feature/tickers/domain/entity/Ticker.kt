package dev.bristot.cryptoapp.feature.tickers.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Ticker(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val prices: Map<CurrencySymbol, Currency>,
    val totalSupply: Long = 0,
    val maxSupply: Long = 0,
    val betaValue: Double = 0.0,
    val firstDataAt: String = "",
    val lastUpdated: String = "",
)
