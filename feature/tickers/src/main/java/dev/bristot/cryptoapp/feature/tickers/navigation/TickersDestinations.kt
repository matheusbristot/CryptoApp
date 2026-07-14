package dev.bristot.cryptoapp.feature.tickers.navigation

import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.RootDestination
import kotlinx.serialization.Serializable

@Serializable
data object TickersDestination : RootDestination

@Serializable
data object RecentTickersDestination : CryptoAppDestination

@Serializable
data class TickerDetailDestination(
    val id: String,
    val name: String,
) : CryptoAppDestination
