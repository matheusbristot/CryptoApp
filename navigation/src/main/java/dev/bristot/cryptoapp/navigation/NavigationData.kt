package dev.bristot.cryptoapp.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope

typealias EntryProviderInstaller = EntryProviderScope<CryptoAppDestination>.() -> Unit

@Stable
class NavigationData(initialDestination: RootDestination) {
    val backStack: SnapshotStateList<CryptoAppDestination> = mutableStateListOf(initialDestination)

    val currentDestination: CryptoAppDestination
        get() = backStack.last()

    fun hasStack() = backStack.size > 1

    fun forward(newDestination: CryptoAppDestination) {
        backStack.add(newDestination)
    }

    fun back() {
        backStack.removeLastOrNull()
    }
}
