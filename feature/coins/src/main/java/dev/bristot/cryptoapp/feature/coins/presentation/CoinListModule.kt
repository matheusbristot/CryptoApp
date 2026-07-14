package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.coins.navigation.CoinsDestination
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive
import dev.bristot.cryptoapp.navigation.RootNavigationDestination
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListViewModel
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.sort.SortViewModel

@Module
@InstallIn(ActivityRetainedComponent::class)
object CoinListModule {

    @IntoSet
    @Provides
    fun provideRootNavigationDestination(
        valueFormatter: CryptoValueFormatter,
        settingsRepository: SettingsRepository,
    ): RootNavigationDestination =
        RootNavigationDestination(
            destination = CoinsDestination,
            label = "Coins",
            icon = Icons.Default.MonetizationOn,
            order = 1,
            entryProviderInstaller = {
                entry<CoinsDestination> {
                    val isActive = LocalNavigationHostActive.current
                    val coinListViewModel = hiltViewModel<CoinListViewModel>()
                    val sortViewModel = hiltViewModel<SortViewModel>()
                    val coinListController = remember(coinListViewModel) {
                        CoinListController(
                            state = coinListViewModel.state,
                            refreshIfNeeded = coinListViewModel::refreshIfNeeded,
                            handleToTop = coinListViewModel::handleToTop,
                            sortBy = coinListViewModel::sortBy,
                        )
                    }
                    val sortController = remember(sortViewModel) {
                        SortController(
                            state = sortViewModel.state,
                            changeType = sortViewModel::changeType,
                            changeOrder = sortViewModel::changeOrder,
                        )
                    }
                    LaunchedEffect(isActive, coinListController) {
                        if (isActive) {
                            settingsRepository.settings.collect {
                                coinListController.refreshIfNeeded()
                            }
                        }
                    }
                    CoinListComponent(
                        controller = coinListController,
                        sortController = sortController,
                        valueFormatter = valueFormatter,
                    )
                }
            },
        )
}
