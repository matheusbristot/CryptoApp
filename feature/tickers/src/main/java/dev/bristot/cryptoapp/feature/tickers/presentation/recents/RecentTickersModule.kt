package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
object RecentTickersModule {

    @IntoSet
    @Provides
    fun provideRecentTickersNavigationData(
        navigationData: NavigationData,
        valueFormatter: CryptoValueFormatter,
    ): EntryProviderInstaller = {
        entry<CryptoAppDestination.RecentTickers> {
            val recentTickersViewModel = hiltViewModel<RecentTickersViewModel>()
            val recentTickersController = remember(recentTickersViewModel) {
                RecentTickersController(
                    state = recentTickersViewModel.state,
                    addRecentTicker = recentTickersViewModel::addRecentTicker,
                )
            }
            RecentTickersContainer(
                recentTickersController = recentTickersController,
                showBackButton = navigationData.hasStack(),
                onBackButtonClick = navigationData::back,
                onSelectTicker = { ticker ->
                    navigationData.forward(
                        CryptoAppDestination.TickerDetail(
                            id = ticker.id,
                            name = ticker.name,
                        )
                    )
                },
                valueFormatter = valueFormatter,
            )
        }
    }
}
