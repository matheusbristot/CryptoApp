package dev.bristot.cryptoapp.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DelegatingSyncWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun doWork_failsWhenTaskKeyIsMissing() = runBlocking {
        assertEquals(
            ListenableWorker.Result.failure(),
            worker(taskKey = null, tasks = emptySet()).doWork(),
        )
    }

    @Test
    fun doWork_failsWhenTaskIsNotRegistered() = runBlocking {
        assertEquals(
            ListenableWorker.Result.failure(),
            worker(taskKey = "missing", tasks = emptySet()).doWork(),
        )
    }

    @Test
    fun doWork_mapsTaskResults() = runBlocking {
        assertEquals(
            ListenableWorker.Result.success(),
            worker("success", setOf(FakeTask("success", SyncResult.Success))).doWork(),
        )
        assertEquals(
            ListenableWorker.Result.failure(),
            worker("failure", setOf(FakeTask("failure", SyncResult.Failure))).doWork(),
        )
        assertEquals(
            ListenableWorker.Result.retry(),
            worker("retry", setOf(FakeTask("retry", SyncResult.Retry)), runAttemptCount = 1).doWork(),
        )
    }

    @Test
    fun doWork_failsWhenRetryBudgetIsExhausted() = runBlocking {
        assertEquals(
            ListenableWorker.Result.failure(),
            worker(
                taskKey = "retry",
                tasks = setOf(FakeTask("retry", SyncResult.Retry)),
                runAttemptCount = DelegatingSyncWorker.MAX_RUN_ATTEMPTS - 1,
            ).doWork(),
        )
    }

    private fun worker(
        taskKey: String?,
        tasks: Set<FeatureSyncTask>,
        runAttemptCount: Int = 0,
    ): DelegatingSyncWorker {
        val inputData = taskKey?.let { key ->
            Data.Builder().putString(DelegatingSyncWorker.INPUT_TASK_KEY, key).build()
        } ?: Data.EMPTY
        return TestListenableWorkerBuilder<DelegatingSyncWorker>(
            context = context,
            inputData = inputData,
            runAttemptCount = runAttemptCount,
        ).setWorkerFactory(
            object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters,
                ): ListenableWorker = DelegatingSyncWorker(
                    appContext = appContext,
                    workerParameters = workerParameters,
                    logger = NoOpLogger,
                    tasks = tasks,
                )
            }
        ).build()
    }

    private class FakeTask(
        override val taskKey: String,
        private val result: SyncResult,
    ) : FeatureSyncTask {
        override val targetType: SyncTargetType = SyncTargetType.COIN
        override val repeatInterval: Duration = 15.minutes
        override suspend fun sync(): SyncResult = result
    }

    private data object NoOpLogger : CryptoLogger {
        override fun debug(message: String, throwable: Throwable?) = Unit
        override fun warning(message: String, throwable: Throwable?) = Unit
        override fun error(throwable: Throwable, message: String) = Unit
    }
}
