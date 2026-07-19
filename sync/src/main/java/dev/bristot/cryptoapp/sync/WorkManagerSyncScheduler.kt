package dev.bristot.cryptoapp.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncScheduler
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal class WorkManagerSyncScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val tasks: Set<@JvmSuppressWildcards FeatureSyncTask>,
    private val targetRegistry: SyncTargetRegistry,
) : SyncScheduler {

    private val reconciliationMutex = Mutex()

    override suspend fun scheduleAll() = reconciliationMutex.withLock {
        validateTaskKeys()

        val workManager = WorkManager.getInstance(context)
        val activeTasks = tasks.filter { task ->
            targetRegistry.idsFor(task.targetType).isNotEmpty()
        }
        val activeTaskKeys = activeTasks.mapTo(mutableSetOf()) { task -> task.taskKey }

        workManager.getWorkInfosByTagFlow(SYNC_TAG).first()
            .filter { workInfo -> workInfo.tags.none(activeTaskKeys::contains) }
            .forEach { workInfo -> workManager.cancelWorkById(workInfo.id) }

        activeTasks.forEach(::schedule)
    }

    override fun cancel(taskKey: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName(taskKey))
    }

    private fun schedule(task: FeatureSyncTask) {
        val request = PeriodicWorkRequest.Builder(
            DelegatingSyncWorker::class.java,
            backgroundInterval(task.repeatInterval).inWholeMilliseconds,
            TimeUnit.MILLISECONDS,
        )
            .setInputData(
                Data.Builder()
                    .putString(DelegatingSyncWorker.INPUT_TASK_KEY, task.taskKey)
                    .build()
            )
            .setConstraints(networkConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(SYNC_TAG)
            .addTag(task.taskKey)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName(task.taskKey),
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    private fun uniqueWorkName(taskKey: String): String = "cryptoapp.sync.$taskKey"

    private fun validateTaskKeys() {
        require(tasks.none { task -> task.taskKey.isBlank() || task.taskKey == SYNC_TAG }) {
            "Sync task keys must be non-blank and distinct from the shared sync tag"
        }
        val duplicatedKeys = tasks.groupingBy { task -> task.taskKey }
            .eachCount()
            .filterValues { count -> count > 1 }
            .keys
        require(duplicatedKeys.isEmpty()) {
            "Duplicate sync task keys: ${duplicatedKeys.joinToString()}"
        }
    }

    companion object {
        const val SYNC_TAG = "cryptoapp.sync"
        val MIN_BACKGROUND_INTERVAL: Duration = 15.minutes

        fun backgroundInterval(requested: Duration): Duration = maxOf(
            requested,
            MIN_BACKGROUND_INTERVAL,
        )

        internal fun networkConstraints(): Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}
