package dev.bristot.cryptoapp.navigation

import androidx.navigation3.runtime.EntryProviderScope
import javax.inject.Inject

class NavigationEntryProviders @Inject constructor(
    private val entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>
) {
    fun asEntryProvider(): EntryProviderScope<CryptoAppDestination>.() -> Unit = {
        entryProviderScopes.forEach { installer -> installer() }
    }
}
