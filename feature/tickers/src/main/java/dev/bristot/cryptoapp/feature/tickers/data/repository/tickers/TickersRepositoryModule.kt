package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TickersRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsTickersRepository(tickersRepositoryImpl: TickersRepositoryImpl): TickersRepository
}
