package dev.bristot.cryptoapp.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope

typealias EntryProviderInstaller = EntryProviderScope<CryptoAppDestination>.() -> Unit

class NavigationData(initialDestination: CryptoAppDestination) {
    val backStack: SnapshotStateList<CryptoAppDestination> = mutableStateListOf(initialDestination)

    fun hasStack() = backStack.size > 1

    fun forward(newDestination: CryptoAppDestination) {
        backStack.add(newDestination)
    }

    fun back() {
        backStack.removeLastOrNull()
    }
}
