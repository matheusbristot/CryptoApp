package dev.bristot.cryptoapp.feature.coins.presentation.detail

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
data class CoinDetailController(
    val state: StateFlow<CoinDetailState>,
    val refreshIfNeeded: () -> Unit,
    val toggleFavorite: () -> Unit,
)
