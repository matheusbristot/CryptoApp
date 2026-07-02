package dev.bristot.cryptoapp.presentation.ticker

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationData

@Module
@InstallIn(ActivityRetainedComponent::class)
object TickerModule {

    @AssistedFactory
    interface TickerViewModelFactory {
        fun create(id: String): TickerViewModel
    }

    @IntoSet
    @Provides
    fun provideTickerNavigationData(navigationData: NavigationData): EntryProviderInstaller = {
        entry<CryptoAppDestination.TickerDetail> { tickerDetail ->
            val tickerViewModel = hiltViewModel<TickerViewModel, TickerViewModelFactory>(
                creationCallback = { factory -> factory.create(tickerDetail.id) })
            TickerContainer(
                name = tickerDetail.name,
                showBackButton = navigationData.hasStack(),
                onBackButtonClick = navigationData::back,
                tickerController = TickerController(
                    state = tickerViewModel.state, onLoadContent = tickerViewModel::getTicker
                )
            )
        }
    }
}
