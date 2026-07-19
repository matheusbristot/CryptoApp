package dev.bristot.cryptoapp.feature.coins.domain.entity

import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency

data class Coin(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val isNew: Boolean,
    val isActive: Boolean,
    val type: String,
    val quote: CoinQuote? = null,
)

data class CoinQuote(
    val currency: QuoteCurrency,
    val price: Double?,
)
