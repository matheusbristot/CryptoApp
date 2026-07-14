package dev.bristot.cryptoapp.feature.settings.presentation

import dev.bristot.cryptoapp.feature.settings.api.AppSettings
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
        val viewModel = SettingsViewModel(repository)

        viewModel.setQuoteEnabled(QuoteCurrency.USD, true)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectQuote(QuoteCurrency.USD)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(QuoteCurrency.USD to true, repository.enabledChange)
        assertEquals(QuoteCurrency.USD, repository.selectedChange)
    }

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
