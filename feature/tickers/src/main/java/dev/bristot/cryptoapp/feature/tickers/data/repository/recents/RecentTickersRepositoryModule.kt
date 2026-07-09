package dev.bristot.cryptoapp.feature.tickers.data.repository.recents

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dev.bristot.cryptoapp.feature.tickers.domain.repository.RecentTickersRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RecentTickersRepositoryModule {

    @Binds
    abstract fun bindsRecentTickersRepository(
        recentTickersRepositoryImpl: RecentTickersRepositoryImpl,
    ): RecentTickersRepository
}
