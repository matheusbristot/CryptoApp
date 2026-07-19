package dev.bristot.cryptoapp.feature.coins.data.local

import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin

fun CoinResponse.toCacheEntity(fetchedAtEpochMillis: Long): CoinCacheEntity = CoinCacheEntity(
    id = id,
    name = name,
    symbol = symbol,
    rank = rank,
    isNew = isNew,
    isActive = isActive,
    type = type,
    fetchedAtEpochMillis = fetchedAtEpochMillis,
)

fun CoinCacheEntity.toDomain(): Coin = Coin(
    id = id,
    name = name,
    symbol = symbol,
    rank = rank,
    isNew = isNew,
    isActive = isActive,
    type = type,
)
