package dev.bristot.cryptoapp

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationCryptoAppHilt
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationRegistry
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive
import dev.bristot.cryptoapp.navigation.LocalNavigationData
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.RootDestination

@Composable
internal fun rememberCryptoAppNavigationState(
    initialRootNavigationData: NavigationData,
    navigationRegistry: NavigationRegistry,
): CryptoAppNavigationState {
    return remember(initialRootNavigationData, navigationRegistry) {
        CryptoAppNavigationState(
            initialRootNavigationData = initialRootNavigationData,
            entryProviderBlock = navigationRegistry.asEntryProvider(),
        )
    }
}

@Stable
internal class CryptoAppNavigationState(
    initialRootNavigationData: NavigationData,
    val entryProviderBlock: EntryProviderInstaller,
) {

    private val initialRootDestination =
        initialRootNavigationData.currentDestination as RootDestination

    private val rootNavigationData = mutableStateMapOf<RootDestination, NavigationData>(
        initialRootDestination to initialRootNavigationData
    )

    var selectedRootDestination: RootDestination by mutableStateOf(
        initialRootDestination
    )
        private set

    val currentDestination: CryptoAppDestination
        get() = selectedNavigationData.currentDestination

    private val selectedNavigationData: NavigationData
        get() = checkNotNull(navigationDataOrNull(selectedRootDestination))

    fun navigationDataOrNull(destination: RootDestination): NavigationData? =
        rootNavigationData[destination]

    fun selectRootDestination(destination: RootDestination) {
        rootNavigationData.getOrPut(destination) {
            NavigationData(destination)
        }
        selectedRootDestination = destination
    }
}

@Composable
internal fun RootNavigationHost(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    navigationData: NavigationData,
    entryProviderBlock: EntryProviderInstaller,
) {
    CompositionLocalProvider(
        LocalNavigationHostActive provides isSelected,
        LocalNavigationData provides navigationData,
    ) {
        NavigationCryptoAppHilt(
            modifier = modifier
                .alpha(if (isSelected) 1f else 0f)
                .zIndex(if (isSelected) 1f else 0f),
            navigationData = navigationData,
            entryProviderBlock = entryProviderBlock,
        )
    }
}

@Composable
internal fun RowScope.CryptoAppNavigationItem(
    destination: RootDestination,
    currentDestination: CryptoAppDestination,
    label: String,
    icon: ImageVector,
    onClick: (RootDestination) -> Unit,
) {
    NavigationBarItem(
        selected = currentDestination == destination,
        onClick = { onClick(destination) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        label = {
            Text(label)
        },
    )
}
