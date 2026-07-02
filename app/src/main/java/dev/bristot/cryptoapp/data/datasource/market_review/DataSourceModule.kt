package dev.bristot.cryptoapp.data.datasource.market_review

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.data.repository.market_review.MarketReviewRepositoryImpl


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindsMarketTickerDataSource(
        marketReviewRemoteDataSourceImpl: MarketReviewRemoteDataSourceImpl
    ): MarketReviewDataSource
}