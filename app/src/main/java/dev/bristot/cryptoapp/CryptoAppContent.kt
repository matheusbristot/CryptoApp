package dev.bristot.cryptoapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationEntryProviders

@Composable
fun CryptoAppContent(
    initialRootNavigationData: NavigationData,
    navigationEntryProviders: NavigationEntryProviders,
) {
    val navigationState = rememberCryptoAppNavigationState(
        initialRootNavigationData = initialRootNavigationData,
        navigationEntryProviders = navigationEntryProviders,
    )
    val rootNavigationItems = rememberCryptoAppRootNavigationItems()
    val currentDestination = navigationState.currentDestination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination is CryptoAppDestination.Root) {
                NavigationBar {
                    rootNavigationItems.forEach { item ->
                        CryptoAppNavigationItem(
                            destination = item.destination,
                            currentDestination = currentDestination,
                            label = item.label,
                            icon = item.icon,
                            onClick = navigationState::selectRootDestination,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            rootNavigationItems.forEach { item ->
                navigationState.navigationDataOrNull(item.destination)?.let { navigationData ->
                    RootNavigationHost(
                        modifier = Modifier.fillMaxSize(),
                        isSelected = navigationState.selectedRootDestination == item.destination,
                        navigationData = navigationData,
                        entryProviderBlock = navigationState.entryProviderBlock,
                    )
                }
            }
        }
    }
}
