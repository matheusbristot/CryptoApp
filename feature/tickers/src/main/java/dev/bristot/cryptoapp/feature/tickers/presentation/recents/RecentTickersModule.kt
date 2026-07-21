package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.tickers.navigation.RecentTickersDestination
import dev.bristot.cryptoapp.feature.tickers.navigation.TickerDetailDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.LocalNavigationData
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object RecentTickersModule {

    @IntoSet
    @Provides
    fun provideRecentTickersNavigationData(
        valueFormatter: CryptoValueFormatter,
        settingsRepository: SettingsRepository,
    ): EntryProviderInstaller = {
        entry<RecentTickersDestination> {
            val navigationData = LocalNavigationData.current
            val settings by settingsRepository.settings.collectAsStateWithLifecycle()
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
                        TickerDetailDestination(
                            id = ticker.id,
                            name = ticker.name,
                        )
                    )
                },
                valueFormatter = valueFormatter,
                quoteCurrency = settings.selectedQuoteCurrency,
            )
        }
    }
}
