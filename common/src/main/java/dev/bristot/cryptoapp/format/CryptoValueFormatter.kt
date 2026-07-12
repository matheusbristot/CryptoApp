package dev.bristot.cryptoapp.format

import androidx.compose.runtime.Stable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@Stable
interface CryptoValueFormatter {
    fun currency(value: Double, currencyCode: String): String
    fun compactCurrency(value: Double, currencyCode: String): String
    fun percentage(value: Double): String
    fun decimal(value: Double, fractionDigits: Int = 2): String
    fun integer(value: Long): String
    fun date(isoInstant: String): String
}

class DefaultCryptoValueFormatter @Inject constructor() : CryptoValueFormatter {
    private val locale: Locale = Locale.getDefault()

    override fun currency(value: Double, currencyCode: String): String =
        NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(currencyCode)
            maximumFractionDigits = if (value.absoluteValue < 1) 6 else 2
        }.format(value)

    override fun compactCurrency(value: Double, currencyCode: String): String {
        val (scaled, suffix) = when {
            value.absoluteValue >= TRILLION -> value / TRILLION to "T"
            value.absoluteValue >= BILLION -> value / BILLION to "B"
            value.absoluteValue >= MILLION -> value / MILLION to "M"
            else -> value to ""
        }
        return "$currencyCode ${decimal(scaled)}$suffix"
    }

    override fun percentage(value: Double): String =
        String.format(locale, "%+.2f%%", value)

    override fun decimal(value: Double, fractionDigits: Int): String =
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }.format(value)

    override fun integer(value: Long): String = NumberFormat.getIntegerInstance(locale).format(value)

    @Suppress("SimpleDateFormat")
    override fun date(isoInstant: String): String = runCatching {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).apply {
            timeZone = UTC
        }
        val parsed: Date = requireNotNull(parser.parse(isoInstant))
        SimpleDateFormat("dd MMM yyyy", locale).format(parsed)
    }.getOrDefault(isoInstant)

    private val Double.absoluteValue: Double get() = kotlin.math.abs(this)

    private companion object {
        val UTC: TimeZone = TimeZone.getTimeZone("UTC")
        const val MILLION = 1_000_000.0
        const val BILLION = 1_000_000_000.0
        const val TRILLION = 1_000_000_000_000.0
    }
}
