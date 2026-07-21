package dev.bristot.cryptoapp.navigation

import androidx.compose.runtime.compositionLocalOf

val LocalNavigationHostActive = compositionLocalOf { true }

val LocalNavigationData = compositionLocalOf<NavigationData> {
    error("NavigationData is not available outside a CryptoApp root host")
}
