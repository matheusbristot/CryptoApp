package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.settings.navigation.SettingsDestination
import dev.bristot.cryptoapp.navigation.RootNavigationDestination

@Module
@InstallIn(ActivityRetainedComponent::class)
object SettingsModule {

    @IntoSet
    @Provides
    fun provideRootNavigationDestination(): RootNavigationDestination =
        RootNavigationDestination(
            destination = SettingsDestination,
            label = "Settings",
            icon = Icons.Default.Settings,
            order = 2,
            entryProviderInstaller = {
                entry<SettingsDestination> {
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
            },
        )
}
