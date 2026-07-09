package dev.bristot.cryptoapp.feature.coins.data.api.coins

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.network.CoinPaprikaRouteFactory
import dev.bristot.cryptoapp.network.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoinsApiModule {

    @Provides
    @Singleton
    fun provideCoinsRoute(routeFactory: CoinPaprikaRouteFactory): CoinsRoutes =
        routeFactory.create<CoinsRoutes>()
}
