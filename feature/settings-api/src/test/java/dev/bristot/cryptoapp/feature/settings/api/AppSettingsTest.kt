package dev.bristot.cryptoapp.feature.settings.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AppSettingsTest {

    @Test
    fun defaultsToBrlQuote() {
        val settings = AppSettings()

        assertEquals(setOf(QuoteCurrency.BRL), settings.requestedQuoteCurrencies)
        assertEquals(QuoteCurrency.BRL, settings.selectedQuoteCurrency)
    }

    @Test
    fun rejectsSelectedQuoteThatIsNotRequested() {
        assertThrows(IllegalArgumentException::class.java) {
            AppSettings(
                requestedQuoteCurrencies = setOf(QuoteCurrency.BRL),
                selectedQuoteCurrency = QuoteCurrency.USD,
            )
        }
    }

    @Test
    fun rejectsMoreThanThreeRequestedQuotes() {
        assertThrows(IllegalArgumentException::class.java) {
            AppSettings(
                requestedQuoteCurrencies = setOf(
                    QuoteCurrency.BRL,
                    QuoteCurrency.USD,
                    QuoteCurrency.EUR,
                    QuoteCurrency.GBP,
                ),
                selectedQuoteCurrency = QuoteCurrency.BRL,
            )
        }
    }
}
