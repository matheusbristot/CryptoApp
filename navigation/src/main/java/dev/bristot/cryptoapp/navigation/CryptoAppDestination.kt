package dev.bristot.cryptoapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CryptoAppDestination : NavKey {
    @Serializable
    object Tickers : CryptoAppDestination

    @Serializable
    data class TickerDetail(val id: String, val name: String) : CryptoAppDestination
}
