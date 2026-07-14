package dev.bristot.cryptoapp.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.AppSettings.Companion.MAX_REQUESTED_QUOTES
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
    dispatcherProvider: DispatcherProvider,
) : SettingsRepository {

    private val dataStore = context.settingsDataStore
    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    override val settings: StateFlow<AppSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map(::preferencesToSettings)
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = AppSettings(),
        )

    override suspend fun setQuoteEnabled(currency: QuoteCurrency, enabled: Boolean) {
        dataStore.edit { preferences ->
            val current = preferencesToSettings(preferences)
            val updatedCurrencies = if (enabled) {
                current.requestedQuoteCurrencies + currency
            } else {
                current.requestedQuoteCurrencies - currency
            }
            if (updatedCurrencies.isEmpty()) return@edit
            if (updatedCurrencies.size > MAX_REQUESTED_QUOTES) return@edit

            val selected = current.selectedQuoteCurrency.takeIf { it in updatedCurrencies }
                ?: updatedCurrencies.sortedBy(QuoteCurrency::name).first()
            preferences[REQUESTED_QUOTES] = updatedCurrencies.map(QuoteCurrency::name).toSet()
            preferences[SELECTED_QUOTE] = selected.name
        }
    }

    override suspend fun selectQuoteCurrency(currency: QuoteCurrency) {
        dataStore.edit { preferences ->
            val current = preferencesToSettings(preferences)
            if (currency !in current.requestedQuoteCurrencies &&
                current.requestedQuoteCurrencies.size >= MAX_REQUESTED_QUOTES
            ) return@edit
            preferences[REQUESTED_QUOTES] =
                (current.requestedQuoteCurrencies + currency).map(QuoteCurrency::name).toSet()
            preferences[SELECTED_QUOTE] = currency.name
        }
    }

    private fun preferencesToSettings(preferences: Preferences): AppSettings {
        val storedSelected = preferences[SELECTED_QUOTE]?.let(::parseCurrency)
        val storedRequested = preferences[REQUESTED_QUOTES]
            .orEmpty()
            .mapNotNull(::parseCurrency)
            .toSet()
        val requested = when {
            storedRequested.isEmpty() -> AppSettings().requestedQuoteCurrencies
            storedRequested.size <= MAX_REQUESTED_QUOTES -> storedRequested
            else -> buildSet {
                storedSelected?.takeIf { it in storedRequested }?.let(::add)
                storedRequested.sortedBy(QuoteCurrency::name).forEach { currency ->
                    if (size < MAX_REQUESTED_QUOTES) add(currency)
                }
            }
        }
        val selected = storedSelected?.takeIf { it in requested }
            ?: QuoteCurrency.BRL.takeIf { it in requested }
            ?: requested.sortedBy(QuoteCurrency::name).first()
        return AppSettings(
            requestedQuoteCurrencies = requested,
            selectedQuoteCurrency = selected,
        )
    }

    private fun parseCurrency(value: String): QuoteCurrency? =
        runCatching { QuoteCurrency.valueOf(value) }.getOrNull()

    private companion object {
        val REQUESTED_QUOTES = stringSetPreferencesKey("requested_quote_currencies")
        val SELECTED_QUOTE = stringPreferencesKey("selected_quote_currency")
    }
}
