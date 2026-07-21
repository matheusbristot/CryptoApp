package dev.bristot.cryptoapp.sync

import androidx.work.WorkInfo
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkManagerSyncStatusObserverTest {

    @Test
    fun selectCurrentWorkInfo_prefersNewestActiveGenerationOverFinishedWork() {
        val finished = workInfo(WorkInfo.State.CANCELLED, generation = 8)
        val oldActive = workInfo(WorkInfo.State.RUNNING, generation = 1)
        val current = workInfo(WorkInfo.State.ENQUEUED, generation = 2)

        assertEquals(current.id, selectCurrentWorkInfo(listOf(finished, oldActive, current), TASK_KEY)?.id)
    }

    @Test
    fun toStatus_mapsScheduledRetryRunningAndFailure() {
        assertEquals(
            SyncWorkState.SCHEDULED,
            task.toStatus(workInfo(WorkInfo.State.ENQUEUED)).state,
        )
        assertEquals(
            SyncWorkState.RETRYING,
            task.toStatus(workInfo(WorkInfo.State.ENQUEUED, runAttemptCount = 1)).state,
        )
        assertEquals(
            SyncWorkState.RUNNING,
            task.toStatus(workInfo(WorkInfo.State.RUNNING)).state,
        )
        assertEquals(
            SyncWorkState.FAILED,
            task.toStatus(workInfo(WorkInfo.State.FAILED)).state,
        )
    }

    @Test
    fun toStatus_exposesFiniteNextScheduleAndHidesSentinelOrFinishedValues() {
        assertEquals(
            1234L,
            task.toStatus(
                workInfo(WorkInfo.State.ENQUEUED, nextScheduleTimeMillis = 1234L)
            ).nextEligibleAtEpochMillis,
        )
        assertNull(
            task.toStatus(
                workInfo(WorkInfo.State.ENQUEUED, nextScheduleTimeMillis = Long.MAX_VALUE)
            ).nextEligibleAtEpochMillis,
        )
        assertNull(
            task.toStatus(
                workInfo(WorkInfo.State.CANCELLED, nextScheduleTimeMillis = 1234L)
            ).nextEligibleAtEpochMillis,
        )
        assertEquals(SyncWorkState.INACTIVE, task.toStatus(null).state)
    }

    private fun workInfo(
        state: WorkInfo.State,
        generation: Int = 0,
        runAttemptCount: Int = 0,
        nextScheduleTimeMillis: Long = Long.MAX_VALUE,
    ): WorkInfo = WorkInfo(
        id = UUID.randomUUID(),
        state = state,
        tags = setOf(TASK_KEY),
        generation = generation,
        runAttemptCount = runAttemptCount,
        nextScheduleTimeMillis = nextScheduleTimeMillis,
    )

    private val task = object : FeatureSyncTask {
        override val taskKey = TASK_KEY
        override val targetType = SyncTargetType.COIN
        override val repeatInterval = 1.minutes
        override suspend fun sync(): SyncResult = SyncResult.Success
    }

    private companion object {
        const val TASK_KEY = "coin-details"
    }
}
