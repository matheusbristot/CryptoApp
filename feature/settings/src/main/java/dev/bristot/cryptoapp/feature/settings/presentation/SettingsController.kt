package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.compose.runtime.Stable
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import kotlinx.coroutines.flow.StateFlow

@Stable
data class SettingsController(
    val settings: StateFlow<AppSettings>,
    val setQuoteEnabled: (currency: QuoteCurrency, enabled: Boolean) -> Unit,
    val selectQuote: (QuoteCurrency) -> Unit,
)
