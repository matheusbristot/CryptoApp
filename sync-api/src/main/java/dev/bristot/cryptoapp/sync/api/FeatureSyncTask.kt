package dev.bristot.cryptoapp.sync.api

import kotlin.time.Duration

interface FeatureSyncTask {
    val taskKey: String
    val targetType: SyncTargetType
    val repeatInterval: Duration

    suspend fun sync(): SyncResult
}

sealed interface SyncResult {
    data object Success : SyncResult
    data object Retry : SyncResult
    data object Failure : SyncResult
}
