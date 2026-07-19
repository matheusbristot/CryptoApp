package dev.bristot.cryptoapp.sync.api

interface SyncScheduler {
    suspend fun scheduleAll()
    fun cancel(taskKey: String)
}
