package dev.bristot.cryptoapp.feature.market_review.presentation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewHeaderRenderer
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewRendererIds
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewRendererKey

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class MarketReviewPresentationModule {

    @Binds
    @IntoMap
    @MarketOverviewRendererKey(MarketOverviewRendererIds.MARKET_REVIEW)
    abstract fun bindMarketReviewHeaderRenderer(
        renderer: MarketReviewHeaderRenderer,
    ): MarketOverviewHeaderRenderer
}
