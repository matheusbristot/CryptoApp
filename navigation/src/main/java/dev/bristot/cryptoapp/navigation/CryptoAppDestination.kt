package dev.bristot.cryptoapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CryptoAppDestination : NavKey {
    @Serializable
    sealed interface Root : CryptoAppDestination

    @Serializable
    object Tickers : Root

    @Serializable
    object Coins : Root

    @Serializable
    object RecentTickers : CryptoAppDestination

    @Serializable
    data class TickerDetail(val id: String, val name: String) : CryptoAppDestination
}
