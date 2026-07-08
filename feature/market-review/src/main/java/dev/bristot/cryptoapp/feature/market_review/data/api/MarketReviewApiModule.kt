package dev.bristot.cryptoapp.feature.market_review.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.feature.market_review.data.api.global.GlobalRoutes
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarketReviewApiModule {

    @Provides
    @Singleton
    fun provideGlobalRoute(client: Retrofit): GlobalRoutes = client.create<GlobalRoutes>()
}
