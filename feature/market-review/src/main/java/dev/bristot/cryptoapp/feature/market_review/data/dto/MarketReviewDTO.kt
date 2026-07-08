package dev.bristot.cryptoapp.feature.market_review.data.dto

import dev.bristot.cryptoapp.feature.market_review.data.model.MarketReviewResponse
import dev.bristot.cryptoapp.feature.market_review.domain.entity.MarketReview

fun MarketReviewResponse.toMarketReview() = MarketReview(
    marketCapUsd = this.marketCapUsd,
    volume24hUsd = this.volume24hUsd,
    bitcoinDominancePercentage = this.bitcoinDominancePercentage,
    cryptocurrenciesNumber = this.cryptocurrenciesNumber,
    marketCapAthValue = this.marketCapAthValue,
    marketCapAthDate = this.marketCapAthDate,
    volume24hAthValue = this.volume24hAthValue,
    volume24hAthDate = this.volume24hAthDate,
    marketCapChange24h = this.marketCapChange24h,
    volume24hChange24h = this.volume24hChange24h,
    lastUpdated = this.lastUpdated
)
