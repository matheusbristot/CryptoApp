package dev.bristot.cryptoapp.sync

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bristot.cryptoapp.sync.api.FeatureSyncStatus
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncStatusObserver
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WorkManagerSyncStatusObserver @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val tasks: Set<@JvmSuppressWildcards FeatureSyncTask>,
) : SyncStatusObserver {

    override fun observe(): Flow<List<FeatureSyncStatus>> = WorkManager.getInstance(context)
        .getWorkInfosByTagFlow(WorkManagerSyncScheduler.SYNC_TAG)
        .map { workInfos ->
            tasks.sortedBy(FeatureSyncTask::taskKey).map { task ->
                val workInfo = selectCurrentWorkInfo(workInfos, task.taskKey)
                task.toStatus(workInfo)
            }
        }
}

internal fun selectCurrentWorkInfo(
    workInfos: List<WorkInfo>,
    taskKey: String,
): WorkInfo? = workInfos
    .filter { workInfo -> taskKey in workInfo.tags }
    .sortedWith(
        compareByDescending<WorkInfo> { workInfo -> !workInfo.state.isFinished }
            .thenByDescending(WorkInfo::generation)
    )
    .firstOrNull()

internal fun FeatureSyncTask.toStatus(workInfo: WorkInfo?): FeatureSyncStatus {
    val state = when (workInfo?.state) {
        WorkInfo.State.RUNNING -> SyncWorkState.RUNNING
        WorkInfo.State.ENQUEUED -> if (workInfo.runAttemptCount > 0) {
            SyncWorkState.RETRYING
        } else {
            SyncWorkState.SCHEDULED
        }
        WorkInfo.State.FAILED -> SyncWorkState.FAILED
        WorkInfo.State.BLOCKED -> SyncWorkState.SCHEDULED
        WorkInfo.State.CANCELLED,
        WorkInfo.State.SUCCEEDED,
        null,
        -> SyncWorkState.INACTIVE
    }
    val nextEligibleAt = workInfo
        ?.takeIf { info -> info.state == WorkInfo.State.ENQUEUED }
        ?.nextScheduleTimeMillis
        ?.takeUnless { value -> value == Long.MAX_VALUE }

    return FeatureSyncStatus(
        targetType = targetType,
        taskKey = taskKey,
        state = state,
        nextEligibleAtEpochMillis = nextEligibleAt,
    )
}
