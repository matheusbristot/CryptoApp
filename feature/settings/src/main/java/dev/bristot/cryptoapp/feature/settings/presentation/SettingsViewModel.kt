package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings = settingsRepository.settings

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
}
