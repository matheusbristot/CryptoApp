package dev.bristot.cryptoapp.sync

import androidx.work.NetworkType
import kotlin.time.Duration.Companion.minutes
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkManagerSyncSchedulerTest {

    @Test
    fun backgroundInterval_clampsIntervalsBelowWorkManagerMinimum() {
        assertEquals(15.minutes, WorkManagerSyncScheduler.backgroundInterval(1.minutes))
        assertEquals(15.minutes, WorkManagerSyncScheduler.backgroundInterval(5.minutes))
    }

    @Test
    fun backgroundInterval_preservesIntervalsAboveWorkManagerMinimum() {
        assertEquals(30.minutes, WorkManagerSyncScheduler.backgroundInterval(30.minutes))
    }

    @Test
    fun networkConstraints_requireAConnectedNetwork() {
        assertEquals(
            NetworkType.CONNECTED,
            WorkManagerSyncScheduler.networkConstraints().requiredNetworkType,
        )
    }
}
