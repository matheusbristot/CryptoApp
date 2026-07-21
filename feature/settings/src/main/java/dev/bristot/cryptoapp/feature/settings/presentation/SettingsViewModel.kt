package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.sync.api.FeatureSyncStatus
import dev.bristot.cryptoapp.sync.api.SyncStatusObserver
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    favoritesRepository: FavoritesRepository,
    syncStatusObserver: SyncStatusObserver,
) : ViewModel() {

    val settings = settingsRepository.settings

    val favoriteSyncStatuses: StateFlow<List<FavoriteSyncUiStatus>> = combine(
        favoritesRepository.observeFavorites(FavoriteType.COIN),
        favoritesRepository.observeFavorites(FavoriteType.TICKER),
        syncStatusObserver.observe(),
    ) { coinFavorites, tickerFavorites, syncStatuses ->
        listOf(
            uiStatus(
                type = FavoriteType.COIN,
                favoriteCount = coinFavorites.size,
                syncStatus = syncStatuses.findFor(SyncTargetType.COIN),
            ),
            uiStatus(
                type = FavoriteType.TICKER,
                favoriteCount = tickerFavorites.size,
                syncStatus = syncStatuses.findFor(SyncTargetType.TICKER),
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = initialSyncStatuses(),
    )

    fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setQuoteEnabled(currency, enabled)
        }
    }

    fun selectQuote(currency: QuoteCurrency) {
        viewModelScope.launch {
            settingsRepository.selectQuoteCurrency(currency)
        }
    }

    private fun uiStatus(
        type: FavoriteType,
        favoriteCount: Int,
        syncStatus: FeatureSyncStatus?,
    ): FavoriteSyncUiStatus = FavoriteSyncUiStatus(
        type = type,
        favoriteCount = favoriteCount,
        state = if (favoriteCount == 0) SyncWorkState.INACTIVE else {
            syncStatus?.state ?: SyncWorkState.INACTIVE
        },
        nextEligibleAtEpochMillis = if (favoriteCount == 0) {
            null
        } else {
            syncStatus?.nextEligibleAtEpochMillis
        },
    )

    private fun List<FeatureSyncStatus>.findFor(type: SyncTargetType): FeatureSyncStatus? =
        firstOrNull { status -> status.targetType == type }

    private fun initialSyncStatuses(): List<FavoriteSyncUiStatus> = listOf(
        FavoriteSyncUiStatus(FavoriteType.COIN, 0, SyncWorkState.INACTIVE, null),
        FavoriteSyncUiStatus(FavoriteType.TICKER, 0, SyncWorkState.INACTIVE, null),
    )
}
