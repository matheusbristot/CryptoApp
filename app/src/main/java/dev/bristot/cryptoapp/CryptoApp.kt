package dev.bristot.cryptoapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationRegistry
import dev.bristot.cryptoapp.sync.api.SyncScheduler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class CryptoApp : Application(), Configuration.Provider {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            syncScheduler.scheduleAll()
        }
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
object CryptoAppModule {

    @Provides
    @ActivityRetainedScoped
    fun provideNavigationData(
        navigationRegistry: NavigationRegistry,
    ): NavigationData = NavigationData(
        initialDestination = navigationRegistry.initialDestination,
    )
}
