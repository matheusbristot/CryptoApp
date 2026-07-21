package dev.bristot.cryptoapp.feature.coins.presentation.detail

import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency

data class CoinDetailState(
    val coin: Coin? = null,
    val quoteCurrency: QuoteCurrency = QuoteCurrency.BRL,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
