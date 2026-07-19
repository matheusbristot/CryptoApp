package dev.bristot.cryptoapp.sync

import dev.bristot.cryptoapp.sync.api.SyncTarget
import dev.bristot.cryptoapp.sync.api.SyncTargetProvider
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSyncTargetRegistryTest {

    @Test
    fun idsFor_mergesTrimsAndDeduplicatesMatchingTargets() = runTest {
        val registry = DefaultSyncTargetRegistry(
            providers = setOf(
                SyncTargetProvider {
                    setOf(
                        SyncTarget(SyncTargetType.COIN, " btc-bitcoin "),
                        SyncTarget(SyncTargetType.COIN, "eth-ethereum"),
                    )
                },
                SyncTargetProvider {
                    setOf(
                        SyncTarget(SyncTargetType.COIN, "btc-bitcoin"),
                        SyncTarget(SyncTargetType.COIN, " "),
                    )
                },
            )
        )

        assertEquals(
            setOf("btc-bitcoin", "eth-ethereum"),
            registry.idsFor(SyncTargetType.COIN),
        )
    }
}
