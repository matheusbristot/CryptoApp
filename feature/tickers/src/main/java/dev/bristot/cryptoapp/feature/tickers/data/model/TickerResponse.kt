package dev.bristot.cryptoapp.feature.tickers.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* **
{
    "id": "btc-bitcoin",
    "name": "Bitcoin",
    "symbol": "BTC",
    "rank": 1,
    "total_supply": 19986169,
    "max_supply": 21000000,
    "beta_value": 0.909039,
    "first_data_at": "2010-07-17T00:00:00Z",
    "last_updated": "2026-02-08T13:52:28Z",
    "quotes":
    {
        "USD": {
        "price": 71370.71020525187,
        "volume_24h": 37555698753.88742,
        "volume_24h_change_24h":-48.7,
        "market_cap": 1426427932261,
        "market_cap_change_24h": 4.13,
        "percent_change_15m": 0.1,
        "percent_change_30m":-0.04,
        "percent_change_1h": 0.19,
        "percent_change_6h": 2.27,
        "percent_change_12h": 2.98,
        "percent_change_24h": 4.13,
        "percent_change_7d":-8.84,
        "percent_change_30d":-21.46,
        "percent_change_1y":-26.32,
        "ath_price": 126173.1777846797,
        "ath_date": "2025-10-06T19:00:40Z",
        "percent_from_price_ath":-43.46
      }
   }
}
* */

@Serializable
data class TickerResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    @SerialName("total_supply") val totalSupply: Long,
    @SerialName("max_supply") val maxSupply: Long,
    @SerialName("beta_value") val betaValue: Double,
    @SerialName("first_data_at") val firstDataAt: String,
    @SerialName("last_updated") val lastUpdated: String,
    val quotes: Map<String, CurrencyResponse>
)
