package dev.bristot.cryptoapp.feature.coins.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class CoinsRepositoryModule {

    @Binds
    abstract fun bindsCoinsRepository(coinsRepositoryImpl: CoinsRepositoryImpl): CoinRepository
}
