package dev.bristot.cryptoapp.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.data.repository.market_review.MarketReviewRepositoryImpl
import dev.bristot.cryptoapp.data.repository.tickers.TickersRepositoryImpl
import dev.bristot.cryptoapp.domain.repository.MarketReviewRepository
import dev.bristot.cryptoapp.domain.repository.TickersRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsTickersRepository(tickersRepositoryImpl: TickersRepositoryImpl): TickersRepository

    @Binds
    abstract fun bindsMarketReviewRepository(marketReviewRepositoryImpl: MarketReviewRepositoryImpl): MarketReviewRepository
}
