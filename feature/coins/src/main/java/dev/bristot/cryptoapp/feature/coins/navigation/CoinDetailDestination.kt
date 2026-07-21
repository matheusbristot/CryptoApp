package dev.bristot.cryptoapp.feature.coins.navigation

import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailDestination(
    val id: String,
    val name: String,
) : CryptoAppDestination
