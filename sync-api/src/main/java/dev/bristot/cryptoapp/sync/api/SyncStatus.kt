package dev.bristot.cryptoapp.sync.api

import kotlinx.coroutines.flow.Flow

enum class SyncWorkState {
    INACTIVE,
    SCHEDULED,
    RUNNING,
    RETRYING,
    FAILED,
}

data class FeatureSyncStatus(
    val targetType: SyncTargetType,
    val taskKey: String,
    val state: SyncWorkState,
    val nextEligibleAtEpochMillis: Long?,
)

fun interface SyncStatusObserver {
    fun observe(): Flow<List<FeatureSyncStatus>>
}
