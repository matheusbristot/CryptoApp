package dev.bristot.cryptoapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    var isBottomBarExpanded by remember { mutableStateOf(true) }
    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {
                    when {
                        available.y < 0f -> isBottomBarExpanded = false
                        available.y > 0f -> isBottomBarExpanded = true
                    }
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(bottomBarScrollConnection),
        bottomBar = {
            if (currentDestination is CryptoAppDestination.Root) {
                AnimatedVisibility(
                    visible = isBottomBarExpanded,
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
                ) {
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
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
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
