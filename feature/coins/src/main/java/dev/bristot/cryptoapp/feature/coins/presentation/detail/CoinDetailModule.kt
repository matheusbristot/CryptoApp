package dev.bristot.cryptoapp.feature.coins.presentation.detail

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.coins.navigation.CoinDetailDestination
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.LocalNavigationData
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive

@Module
@InstallIn(ActivityRetainedComponent::class)
object CoinDetailModule {

    @AssistedFactory
    interface CoinDetailViewModelFactory {
        fun create(coinId: String): CoinDetailViewModel
    }

    @IntoSet
    @Provides
    fun provideCoinDetailEntry(
        valueFormatter: CryptoValueFormatter,
        settingsRepository: SettingsRepository,
    ): EntryProviderInstaller = {
        entry<CoinDetailDestination> { destination ->
            val navigationData = LocalNavigationData.current
            val isActive = LocalNavigationHostActive.current
            val viewModel = hiltViewModel<CoinDetailViewModel, CoinDetailViewModelFactory>(
                creationCallback = { factory -> factory.create(destination.id) },
            )
            val controller = remember(viewModel) {
                CoinDetailController(
                    state = viewModel.state,
                    refreshIfNeeded = viewModel::refreshIfNeeded,
                    toggleFavorite = viewModel::toggleFavorite,
                )
            }

            LaunchedEffect(isActive, controller) {
                if (isActive) {
                    settingsRepository.settings.collect { controller.refreshIfNeeded() }
                }
            }

            CoinDetailComponent(
                name = destination.name,
                controller = controller,
                valueFormatter = valueFormatter,
                showBackButton = navigationData.hasStack(),
                onBack = navigationData::back,
            )
        }
    }
}
