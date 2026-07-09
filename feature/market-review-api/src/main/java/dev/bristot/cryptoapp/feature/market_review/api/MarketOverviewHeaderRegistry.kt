package dev.bristot.cryptoapp.feature.market_review.api

import javax.inject.Inject

class MarketOverviewHeaderRegistry @Inject constructor(
    private val renderers: Map<String, @JvmSuppressWildcards MarketOverviewHeaderRenderer>,
) {

    fun required(id: String): MarketOverviewHeaderRenderer {
        return requireNotNull(renderers[id]) {
            "Missing market overview renderer: $id"
        }
    }

    fun optional(id: String): MarketOverviewHeaderRenderer? = renderers[id]
}
