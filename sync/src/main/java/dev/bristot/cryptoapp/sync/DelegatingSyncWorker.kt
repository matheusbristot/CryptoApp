package dev.bristot.cryptoapp.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncResult

@HiltWorker
class DelegatingSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val logger: CryptoLogger,
    private val tasks: Set<@JvmSuppressWildcards FeatureSyncTask>,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val taskKey = inputData.getString(INPUT_TASK_KEY) ?: return Result.failure()
        val task = tasks.singleOrNull { candidate -> candidate.taskKey == taskKey }
            ?: return Result.failure()

        return when (task.sync()) {
            SyncResult.Success -> Result.success()
            SyncResult.Failure -> Result.failure()
            SyncResult.Retry -> {
                if (runAttemptCount + 1 < MAX_RUN_ATTEMPTS) {
                    Result.retry()
                } else {
                    logger.warning(message = "Sync $taskKey exhausted its retry budget")
                    Result.failure()
                }
            }
        }
    }

    companion object {
        const val INPUT_TASK_KEY = "sync_task_key"
        internal const val MAX_RUN_ATTEMPTS = 3
    }
}
