package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import kotlinx.coroutines.flow.StateFlow

@Stable
data class SettingsController(
    val settings: StateFlow<AppSettings>,
    val favoriteSyncStatuses: StateFlow<List<FavoriteSyncUiStatus>>,
    val setQuoteEnabled: (currency: QuoteCurrency, enabled: Boolean) -> Unit,
    val selectQuote: (QuoteCurrency) -> Unit,
)

data class FavoriteSyncUiStatus(
    val type: FavoriteType,
    val favoriteCount: Int,
    val state: SyncWorkState,
    val nextEligibleAtEpochMillis: Long?,
)
