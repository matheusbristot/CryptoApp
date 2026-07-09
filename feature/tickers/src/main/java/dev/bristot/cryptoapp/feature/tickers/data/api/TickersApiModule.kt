package dev.bristot.cryptoapp.feature.tickers.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.tickers.data.api.tickers.TickersRoutes
import dev.bristot.cryptoapp.network.CoinPaprikaRouteFactory
import dev.bristot.cryptoapp.network.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TickersApiModule {

    @Provides
    @Singleton
    fun provideTickersRoute(routeFactory: CoinPaprikaRouteFactory): TickersRoutes =
        routeFactory.create<TickersRoutes>()
}
