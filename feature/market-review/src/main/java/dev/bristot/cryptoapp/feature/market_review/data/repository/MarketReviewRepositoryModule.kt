package dev.bristot.cryptoapp.feature.market_review.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.market_review.domain.repository.MarketReviewRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class MarketReviewRepositoryModule {

    @Binds
    abstract fun bindsMarketReviewRepository(
        marketReviewRepositoryImpl: MarketReviewRepositoryImpl
    ): MarketReviewRepository
}
