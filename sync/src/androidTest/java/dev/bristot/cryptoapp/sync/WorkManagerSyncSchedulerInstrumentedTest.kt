package dev.bristot.cryptoapp.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkManagerSyncSchedulerInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @After
    fun tearDown() {
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun scheduleAll_enqueuesOneUniquePeriodicWorkForActiveTask() = runBlocking {
        scheduler(tasks = setOf(FakeTask("coin-details")), ids = setOf("btc-bitcoin"))
            .scheduleAll()

        val workInfos = workInfos("coin-details")

        assertEquals(1, workInfos.size)
        assertTrue(workInfos.single().tags.contains(WorkManagerSyncScheduler.SYNC_TAG))
        assertTrue(workInfos.single().tags.contains("coin-details"))
    }

    @Test
    fun scheduleAll_updatesExistingUniquePeriodicWork() = runBlocking {
        scheduler(
            tasks = setOf(FakeTask("coin-details", repeatInterval = 15.minutes)),
            ids = setOf("btc-bitcoin"),
        ).scheduleAll()
        val original = workInfos("coin-details").single()

        scheduler(
            tasks = setOf(FakeTask("coin-details", repeatInterval = 30.minutes)),
            ids = setOf("btc-bitcoin"),
        ).scheduleAll()
        val updated = workInfos("coin-details").single()

        assertEquals(original.id, updated.id)
        assertTrue(updated.generation > original.generation)
    }

    @Test
    fun scheduleAll_cancelsWorkWhenTaskNoLongerHasTargets() = runBlocking {
        scheduler(tasks = setOf(FakeTask("coin-details")), ids = setOf("btc-bitcoin"))
            .scheduleAll()

        scheduler(tasks = setOf(FakeTask("coin-details")), ids = emptySet())
            .scheduleAll()

        assertEquals(WorkInfo.State.CANCELLED, workInfos("coin-details").single().state)
    }

    @Test
    fun scheduleAll_cancelsOrphanedTask() = runBlocking {
        scheduler(tasks = setOf(FakeTask("old-task")), ids = setOf("btc-bitcoin"))
            .scheduleAll()

        scheduler(tasks = setOf(FakeTask("new-task")), ids = setOf("btc-bitcoin"))
            .scheduleAll()

        assertEquals(WorkInfo.State.CANCELLED, workInfos("old-task").single().state)
        assertEquals(WorkInfo.State.ENQUEUED, workInfos("new-task").single().state)
    }

    @Test
    fun cancel_cancelsUniqueWorkByTaskKey() = runBlocking {
        val scheduler = scheduler(
            tasks = setOf(FakeTask("coin-details")),
            ids = setOf("btc-bitcoin"),
        )
        scheduler.scheduleAll()

        scheduler.cancel("coin-details")

        assertEquals(WorkInfo.State.CANCELLED, workInfos("coin-details").single().state)
    }

    private fun scheduler(
        tasks: Set<FeatureSyncTask>,
        ids: Set<String>,
    ) = WorkManagerSyncScheduler(
        context = context,
        tasks = tasks,
        targetRegistry = object : SyncTargetRegistry {
            override suspend fun idsFor(type: SyncTargetType): Set<String> = ids
        },
    )

    private fun workInfos(taskKey: String): List<WorkInfo> = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork("cryptoapp.sync.$taskKey")
        .get(5, TimeUnit.SECONDS)

    private class FakeTask(
        override val taskKey: String,
        override val repeatInterval: Duration = 15.minutes,
    ) : FeatureSyncTask {
        override val targetType: SyncTargetType = SyncTargetType.COIN
        override suspend fun sync(): SyncResult = SyncResult.Success
    }
}
