package dev.bristot.cryptoapp

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
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
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationCryptoAppHilt
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationEntryProviders
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive

@Composable
internal fun rememberCryptoAppRootNavigationItems(): List<CryptoAppRootNavigationItem> = remember {
    listOf(
        CryptoAppRootNavigationItem(
            destination = CryptoAppDestination.Tickers,
            label = "Tickers",
            icon = Icons.AutoMirrored.Filled.ShowChart,
        ),
        CryptoAppRootNavigationItem(
            destination = CryptoAppDestination.Coins,
            label = "Coins",
            icon = Icons.Default.MonetizationOn,
        ),
        CryptoAppRootNavigationItem(
            destination = CryptoAppDestination.Settings,
            label = "Settings",
            icon = Icons.Default.Settings,
        ),
    )
}

@Immutable
internal data class CryptoAppRootNavigationItem(
    val destination: CryptoAppDestination.Root,
    val label: String,
    val icon: ImageVector,
)

@Composable
internal fun rememberCryptoAppNavigationState(
    initialRootNavigationData: NavigationData,
    navigationEntryProviders: NavigationEntryProviders,
): CryptoAppNavigationState {
    return remember(initialRootNavigationData, navigationEntryProviders) {
        CryptoAppNavigationState(
            initialRootNavigationData = initialRootNavigationData,
            entryProviderBlock = navigationEntryProviders.asEntryProvider(),
        )
    }
}

@Stable
internal class CryptoAppNavigationState(
    initialRootNavigationData: NavigationData,
    val entryProviderBlock: EntryProviderInstaller,
) {

    private val initialRootDestination =
        initialRootNavigationData.currentDestination as CryptoAppDestination.Root

    private val rootNavigationData = mutableStateMapOf(
        initialRootDestination to initialRootNavigationData
    )

    var selectedRootDestination: CryptoAppDestination.Root by mutableStateOf(
        initialRootDestination
    )
        private set

    val currentDestination: CryptoAppDestination
        get() = selectedNavigationData.currentDestination

    private val selectedNavigationData: NavigationData
        get() = checkNotNull(navigationDataOrNull(selectedRootDestination))

    fun navigationDataOrNull(destination: CryptoAppDestination.Root): NavigationData? =
        rootNavigationData[destination]

    fun selectRootDestination(destination: CryptoAppDestination.Root) {
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
    CompositionLocalProvider(LocalNavigationHostActive provides isSelected) {
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
    destination: CryptoAppDestination.Root,
    currentDestination: CryptoAppDestination,
    label: String,
    icon: ImageVector,
    onClick: (CryptoAppDestination.Root) -> Unit,
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
