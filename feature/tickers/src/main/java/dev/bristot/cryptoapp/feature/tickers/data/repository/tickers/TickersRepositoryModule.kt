package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class TickersRepositoryModule {

    @Binds
    abstract fun bindsTickersRepository(tickersRepositoryImpl: TickersRepositoryImpl): TickersRepository
}
