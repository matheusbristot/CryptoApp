package dev.bristot.cryptoapp.feature.settings.presentation

import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteRef
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.sync.api.FeatureSyncStatus
import dev.bristot.cryptoapp.sync.api.SyncStatusObserver
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun delegatesQuoteChangesToRepository() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(
            settingsRepository = repository,
            favoritesRepository = EmptyFavoritesRepository,
            syncStatusObserver = SyncStatusObserver { flowOf(emptyList<FeatureSyncStatus>()) },
        )

        viewModel.setQuoteEnabled(QuoteCurrency.USD, true)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectQuote(QuoteCurrency.USD)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(QuoteCurrency.USD to true, repository.enabledChange)
        assertEquals(QuoteCurrency.USD, repository.selectedChange)
    }

    @Test
    fun favoriteSyncStatuses_reactIndependentlyAndBecomeInactiveAtZero() = runTest {
        val favoritesRepository = MutableFavoritesRepository()
        val syncStatuses = MutableStateFlow(
            listOf(
                FeatureSyncStatus(
                    targetType = SyncTargetType.COIN,
                    taskKey = "coin-details",
                    state = SyncWorkState.SCHEDULED,
                    nextEligibleAtEpochMillis = 100L,
                ),
                FeatureSyncStatus(
                    targetType = SyncTargetType.TICKER,
                    taskKey = "ticker-details",
                    state = SyncWorkState.FAILED,
                    nextEligibleAtEpochMillis = null,
                ),
            )
        )
        val viewModel = SettingsViewModel(
            settingsRepository = FakeSettingsRepository(),
            favoritesRepository = favoritesRepository,
            syncStatusObserver = SyncStatusObserver { syncStatuses },
        )
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.favoriteSyncStatuses.collect()
        }

        favoritesRepository.coinFavorites.value = listOf(
            favorite(FavoriteType.COIN, "btc-bitcoin"),
            favorite(FavoriteType.COIN, "eth-ethereum"),
        )
        favoritesRepository.tickerFavorites.value = listOf(
            favorite(FavoriteType.TICKER, "sol-solana"),
        )
        advanceUntilIdle()

        assertEquals(
            FavoriteSyncUiStatus(FavoriteType.COIN, 2, SyncWorkState.SCHEDULED, 100L),
            viewModel.favoriteSyncStatuses.value[0],
        )
        assertEquals(
            FavoriteSyncUiStatus(FavoriteType.TICKER, 1, SyncWorkState.FAILED, null),
            viewModel.favoriteSyncStatuses.value[1],
        )

        favoritesRepository.coinFavorites.value = emptyList()
        advanceUntilIdle()

        assertEquals(
            FavoriteSyncUiStatus(FavoriteType.COIN, 0, SyncWorkState.INACTIVE, null),
            viewModel.favoriteSyncStatuses.value[0],
        )
        assertEquals(1, viewModel.favoriteSyncStatuses.value[1].favoriteCount)
        collectJob.cancel()
    }

    private object EmptyFavoritesRepository : FavoritesRepository {
        override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> =
            flowOf(emptyList())

        override fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean> =
            flowOf(false)

        override suspend fun setFavorite(
            type: FavoriteType,
            itemId: String,
            isFavorite: Boolean,
        ) = Unit
    }

    private class MutableFavoritesRepository : FavoritesRepository {
        val coinFavorites = MutableStateFlow<List<FavoriteRef>>(emptyList())
        val tickerFavorites = MutableStateFlow<List<FavoriteRef>>(emptyList())

        override fun observeFavorites(type: FavoriteType): Flow<List<FavoriteRef>> = when (type) {
            FavoriteType.COIN -> coinFavorites
            FavoriteType.TICKER -> tickerFavorites
        }

        override fun observeIsFavorite(type: FavoriteType, itemId: String): Flow<Boolean> =
            flowOf(false)

        override suspend fun setFavorite(
            type: FavoriteType,
            itemId: String,
            isFavorite: Boolean,
        ) = Unit
    }

    private fun favorite(type: FavoriteType, itemId: String) = FavoriteRef(
        type = type,
        itemId = itemId,
        createdAtEpochMillis = 1L,
    )

    private class FakeSettingsRepository : SettingsRepository {
        override val settings = MutableStateFlow(AppSettings())
        var enabledChange: Pair<QuoteCurrency, Boolean>? = null
        var selectedChange: QuoteCurrency? = null

        override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) {
            enabledChange = currency to enabled
        }

        override suspend fun selectQuoteCurrency(currency: QuoteCurrency) {
            selectedChange = currency
        }
    }
}
