package dev.bristot.cryptoapp.feature.settings.presentation

import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import dev.bristot.cryptoapp.feature.favorites.api.FavoriteType
import dev.bristot.cryptoapp.sync.api.SyncWorkState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class SettingsControllerTest {

    @Test
    fun keepsStateAndDelegatesActions() {
        val settings = MutableStateFlow(AppSettings())
        var enabledChange: Pair<QuoteCurrency, Boolean>? = null
        var selectedQuote: QuoteCurrency? = null
        val controller = SettingsController(
            settings = settings,
            favoriteSyncStatuses = MutableStateFlow(
                listOf(FavoriteSyncUiStatus(FavoriteType.COIN, 0, SyncWorkState.INACTIVE, null))
            ),
            setQuoteEnabled = { currency, enabled -> enabledChange = currency to enabled },
            selectQuote = { selectedQuote = it },
        )

        assertSame(settings, controller.settings)
        controller.setQuoteEnabled(QuoteCurrency.USD, true)
        controller.selectQuote(QuoteCurrency.USD)

        assertEquals(QuoteCurrency.USD to true, enabledChange)
        assertEquals(QuoteCurrency.USD, selectedQuote)
    }
}
