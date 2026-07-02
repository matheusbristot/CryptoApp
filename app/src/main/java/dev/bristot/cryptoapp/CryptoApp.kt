package dev.bristot.cryptoapp

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.NavigationData

@HiltAndroidApp
class CryptoApp : Application()

@Module
@InstallIn(ActivityRetainedComponent::class)
object CryptoAppModule {

    @Provides
    @ActivityRetainedScoped
    fun provideNavigationData(): NavigationData =
        NavigationData(initialDestination = CryptoAppDestination.Tickers)
}
