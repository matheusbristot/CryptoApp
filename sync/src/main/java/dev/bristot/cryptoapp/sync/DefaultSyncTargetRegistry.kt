package dev.bristot.cryptoapp.sync

import dev.bristot.cryptoapp.sync.api.SyncTargetProvider
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import javax.inject.Inject

internal class DefaultSyncTargetRegistry @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards SyncTargetProvider>,
) : SyncTargetRegistry {

    override suspend fun idsFor(type: SyncTargetType): Set<String> = providers
        .flatMap { provider -> provider.targets() }
        .asSequence()
        .filter { target -> target.type == type }
        .map { target -> target.id.trim() }
        .filter { id -> id.isNotEmpty() }
        .toSet()
}
