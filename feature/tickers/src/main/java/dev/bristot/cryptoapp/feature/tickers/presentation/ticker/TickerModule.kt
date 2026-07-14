package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.Module
import dagger.Provides
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.tickers.navigation.TickerDetailDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import javax.inject.Provider

@Module
@InstallIn(ActivityRetainedComponent::class)
object TickerModule {

    @AssistedFactory
    interface TickerViewModelFactory {
        fun create(id: String): TickerViewModel
    }

    @IntoSet
    @Provides
    fun provideTickerNavigationData(
        navigationDataProvider: Provider<NavigationData>,
        valueFormatter: CryptoValueFormatter,
        settingsRepository: SettingsRepository,
    ): EntryProviderInstaller = {
        entry<TickerDetailDestination> { tickerDetail ->
            val navigationData = navigationDataProvider.get()
            val isActive = LocalNavigationHostActive.current
            val tickerViewModel = hiltViewModel<TickerViewModel, TickerViewModelFactory>(
                creationCallback = { factory -> factory.create(tickerDetail.id) })
            val tickerController = remember(tickerViewModel) {
                TickerController(
                    state = tickerViewModel.state,
                    quoteCurrency = tickerViewModel.quoteCurrency,
                    refreshIfNeeded = tickerViewModel::refreshIfNeeded,
                )
            }
            val quoteCurrency by tickerController.quoteCurrency.collectAsStateWithLifecycle()
            LaunchedEffect(isActive, tickerController) {
                if (isActive) {
                    settingsRepository.settings.collect {
                        tickerController.refreshIfNeeded()
                    }
                }
            }
            TickerContainer(
                name = tickerDetail.name,
                showBackButton = navigationData.hasStack(),
                onBackButtonClick = navigationData::back,
                tickerController = tickerController,
                valueFormatter = valueFormatter,
                quoteCurrency = quoteCurrency,
            )
        }
    }
}
