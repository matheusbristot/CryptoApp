package dev.bristot.cryptoapp.feature.market_review.data.datasource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class MarketReviewDataSourceModule {

    @Binds
    abstract fun bindsMarketReviewDataSource(
        marketReviewRemoteDataSourceImpl: MarketReviewRemoteDataSourceImpl
    ): MarketReviewDataSource
}
