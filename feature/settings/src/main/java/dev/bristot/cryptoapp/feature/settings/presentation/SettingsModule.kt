package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object SettingsModule {

    @IntoSet
    @Provides
    fun provideSettingsNavigationData(): EntryProviderInstaller = {
        entry<CryptoAppDestination.Settings> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            val controller = remember(viewModel) {
                SettingsController(
                    settings = viewModel.settings,
                    setQuoteEnabled = viewModel::setQuoteEnabled,
                    selectQuote = viewModel::selectQuote,
                )
            }
            SettingsComponent(controller = controller)
        }
    }
}
