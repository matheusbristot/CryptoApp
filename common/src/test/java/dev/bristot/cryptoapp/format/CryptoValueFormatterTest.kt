package dev.bristot.cryptoapp.format

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class CryptoValueFormatterTest {
    private lateinit var previousLocale: Locale
    private lateinit var formatter: CryptoValueFormatter

    @Before fun setUpLocale() {
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
        formatter = DefaultCryptoValueFormatter()
    }

    @After fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test fun formatsCurrencyWithPrecisionSuitableForCrypto() {
        assertEquals("\$0.123457", formatter.currency(0.1234567, "USD"))
        assertEquals("\$1,234.50", formatter.currency(1_234.5, "USD"))
    }

    @Test fun formatsCompactCurrencyAndSignedPercentage() {
        assertEquals("USD 1.25B", formatter.compactCurrency(1_250_000_000.0, "USD"))
        assertEquals("-2.50%", formatter.percentage(-2.5))
        assertEquals("+2.50%", formatter.percentage(2.5))
    }

    @Test fun formatsIntegerDecimalAndIsoDate() {
        assertEquals("21,000,000", formatter.integer(21_000_000))
        assertEquals("0.91", formatter.decimal(0.909))
        assertEquals("06 Oct 2025", formatter.date("2025-10-06T19:00:40Z"))
        assertEquals("unknown", formatter.date("unknown"))
    }
}
