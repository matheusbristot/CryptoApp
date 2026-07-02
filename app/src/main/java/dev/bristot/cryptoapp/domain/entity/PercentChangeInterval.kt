package dev.bristot.cryptoapp.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PercentChangeInterval(
    val p15m: Double,
    val p30m: Double,
    val p1h: Double,
    val p6h: Double,
    val p12h: Double,
    val p24h: Double,
    val p7d: Double,
    val p30d: Double,
    val p1y: Double,
)
