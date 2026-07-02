package dev.bristot.cryptoapp.data.datasource.tickers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.data.api.tickers.TickersRoutes
import dev.bristot.cryptoapp.logger.CryptoLogger

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindsTickerDataSource(
        tickersRemoteDataSourceImpl: TickersRemoteDataSourceImpl
    ): TickersDataSource
}