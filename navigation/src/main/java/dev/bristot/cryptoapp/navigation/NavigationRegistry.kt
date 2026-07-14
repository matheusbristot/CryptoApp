package dev.bristot.cryptoapp.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import javax.inject.Inject

@Stable
class RootNavigationDestination(
    val destination: RootDestination,
    val label: String,
    val icon: ImageVector,
    val order: Int,
    internal val entryProviderInstaller: EntryProviderInstaller,
)

/**
 * Single navigation registry exposed to the application.
 *
 * Root destinations carry both their tab metadata and entry installer. The separate
 * [EntryProviderInstaller] set is therefore reserved for non-root destinations.
 */
@Stable
class NavigationRegistry @Inject constructor(
    rootDestinations: Set<@JvmSuppressWildcards RootNavigationDestination>,
    private val nonRootEntryProviders: Set<@JvmSuppressWildcards EntryProviderInstaller>,
) {
    val rootDestinations: List<RootNavigationDestination> = rootDestinations.sortedBy { it.order }

    val initialDestination: RootDestination
        get() = requireNotNull(rootDestinations.firstOrNull()) {
            "At least one root navigation destination must be registered with Hilt"
        }.destination

    private val entryProvider: EntryProviderScope<CryptoAppDestination>.() -> Unit = {
        rootDestinations.forEach { root -> root.entryProviderInstaller(this) }
        nonRootEntryProviders.forEach { installer -> installer(this) }
    }

    fun asEntryProvider(): EntryProviderInstaller = entryProvider
}
