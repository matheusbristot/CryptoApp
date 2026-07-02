package dev.bristot.cryptoapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    @SerialName("is_new") val isNew: Boolean,
    @SerialName("is_active") val isActive: Boolean,
    val type: String
)