package dev.bristot.cryptoapp.feature.settings.api

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean)

    suspend fun selectQuoteCurrency(currency: QuoteCurrency)
}
