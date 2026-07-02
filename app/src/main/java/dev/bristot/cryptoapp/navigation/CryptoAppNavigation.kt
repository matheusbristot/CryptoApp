package dev.bristot.cryptoapp.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavigationCryptoAppHilt(
    modifier: Modifier = Modifier,
    navigationData: NavigationData,
    entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>
) {

    NavDisplay(
        modifier = modifier,
        backStack = navigationData.backStack,
        onBack = navigationData::back,
        transitionSpec = {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 4 }
        },
        popTransitionSpec = {
            slideInHorizontally { -it / 4 } togetherWith slideOutHorizontally { it }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally { -it / 4 } togetherWith slideOutHorizontally { it }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider { entryProviderScopes.forEach { builder -> this.builder() } },
    )
}