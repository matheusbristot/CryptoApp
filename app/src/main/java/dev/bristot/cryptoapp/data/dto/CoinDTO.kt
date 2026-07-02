package dev.bristot.cryptoapp.data.dto

import dev.bristot.cryptoapp.data.model.CoinResponse
import dev.bristot.cryptoapp.domain.entity.Coin

fun CoinResponse.coinDTO() = Coin(
    id = this.id,
    name = this.name,
    symbol = this.symbol,
    rank = this.rank,
    isNew = this.isNew,
    isActive = this.isActive,
    type = this.type,
)
