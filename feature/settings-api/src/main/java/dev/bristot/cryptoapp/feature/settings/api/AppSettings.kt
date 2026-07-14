package dev.bristot.cryptoapp.feature.settings.api

data class AppSettings(
    val requestedQuoteCurrencies: Set<QuoteCurrency> = setOf(QuoteCurrency.BRL),
    val selectedQuoteCurrency: QuoteCurrency = QuoteCurrency.BRL,
) {
    init {
        require(requestedQuoteCurrencies.isNotEmpty()) {
            "At least one quote currency must be requested"
        }
        require(selectedQuoteCurrency in requestedQuoteCurrencies) {
            "The selected quote currency must also be requested"
        }
        require(requestedQuoteCurrencies.size <= MAX_REQUESTED_QUOTES) {
            "At most $MAX_REQUESTED_QUOTES quote currencies can be requested"
        }
    }

    companion object {
        const val MAX_REQUESTED_QUOTES = 3
    }
}
