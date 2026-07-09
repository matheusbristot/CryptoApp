package dev.bristot.cryptoapp.feature.coins.data.datasource.coins

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoinsDataSourceModule {

    @Binds
    abstract fun bindsCoinsDataSource(
        coinsRemoteDataSourceImpl: CoinsRemoteDataSourceImpl
    ): CoinsDatasource
}
