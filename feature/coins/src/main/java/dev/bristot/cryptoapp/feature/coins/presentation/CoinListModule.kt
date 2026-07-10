package dev.bristot.cryptoapp.feature.coins.presentation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object CoinListModule {

    @IntoSet
    @Provides
    fun provideCoinListNavigationData(): EntryProviderInstaller = {
        entry<CryptoAppDestination.Coins> {
            CoinListComponent()
        }
    }
}
