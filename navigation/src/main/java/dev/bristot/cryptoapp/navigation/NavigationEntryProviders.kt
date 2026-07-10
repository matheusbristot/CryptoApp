package dev.bristot.cryptoapp.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.EntryProviderScope
import javax.inject.Inject

@Stable
class NavigationEntryProviders @Inject constructor(
    private val entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>
) {
    private val entryProvider: EntryProviderScope<CryptoAppDestination>.() -> Unit = {
        entryProviderScopes.forEach { installer -> installer() }
    }

    fun asEntryProvider(): EntryProviderScope<CryptoAppDestination>.() -> Unit = entryProvider
}
