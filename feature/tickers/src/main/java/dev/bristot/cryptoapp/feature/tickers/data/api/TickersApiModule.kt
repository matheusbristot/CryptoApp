package dev.bristot.cryptoapp.feature.tickers.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.tickers.data.api.tickers.TickersRoutes
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TickersApiModule {

    @Provides
    @Singleton
    fun provideTickersRoute(client: Retrofit): TickersRoutes = client.create<TickersRoutes>()
}
