package dev.bristot.cryptoapp.sync

import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncScheduler
import dev.bristot.cryptoapp.sync.api.SyncTargetProvider
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SyncBindingsModule {

    @Binds
    @Singleton
    abstract fun bindSyncScheduler(implementation: WorkManagerSyncScheduler): SyncScheduler

    @Binds
    @Singleton
    abstract fun bindSyncTargetRegistry(implementation: DefaultSyncTargetRegistry): SyncTargetRegistry

    @Multibinds
    abstract fun syncTasks(): Set<FeatureSyncTask>

    @Multibinds
    abstract fun syncTargetProviders(): Set<SyncTargetProvider>
}
