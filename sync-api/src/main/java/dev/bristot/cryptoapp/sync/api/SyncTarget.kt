package dev.bristot.cryptoapp.sync.api

data class SyncTarget(
    val type: SyncTargetType,
    val id: String,
)

enum class SyncTargetType {
    COIN,
    TICKER,
}

fun interface SyncTargetProvider {
    suspend fun targets(): Set<SyncTarget>
}

interface SyncTargetRegistry {
    suspend fun idsFor(type: SyncTargetType): Set<String>
}
