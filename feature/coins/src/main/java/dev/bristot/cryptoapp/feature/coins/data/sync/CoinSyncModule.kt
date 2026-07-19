package dev.bristot.cryptoapp.feature.coins.data.sync

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask

@Module
@InstallIn(SingletonComponent::class)
abstract class CoinSyncModule {

    @Binds
    @IntoSet
    abstract fun bindCoinSyncTask(task: CoinSyncTask): FeatureSyncTask
}
