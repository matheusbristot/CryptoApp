package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers.TickersDataSource
import dev.bristot.cryptoapp.feature.tickers.data.model.CurrencyResponse
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TickersRepositoryImplTest {

    @Test
    fun getTickers_passesCurrencyNamesToDataSourceAndMapsResponse() = runBlocking {
        val dataSource = FakeTickersDataSource(
            tickers = listOf(tickerResponse(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)),
            ticker = tickerResponse(id = "placeholder", name = "placeholder", symbol = "P", rank = 0),
        )
        val repository = TickersRepositoryImpl(tickersDataSource = dataSource)

        val tickers = repository.getTickers(currencies = setOf(CurrencySymbol.BRL)).first()

        assertEquals(listOf("BRL"), dataSource.requestedTickersCurrencies)
        assertEquals(
            listOf(
                Ticker(
                    id = "btc",
                    name = "Bitcoin",
                    symbol = "BTC",
                    rank = 1,
                    prices = mapOf(
                        CurrencySymbol.BRL to expectedCurrency(price = 71420.0),
                    ),
                )
            ),
            tickers
        )
    }

    @Test
    fun getTicker_passesCurrencyNamesToDataSourceAndMapsResponse() = runBlocking {
        val dataSource = FakeTickersDataSource(
            ticker = tickerResponse(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)
        )
        val repository = TickersRepositoryImpl(tickersDataSource = dataSource)

        val ticker = repository.getTicker(coinId = "eth-ethereum", currencies = setOf(CurrencySymbol.USD)).first()

        assertEquals("eth-ethereum", dataSource.requestedTickerCoinId)
        assertEquals(listOf("USD"), dataSource.requestedTickerCurrencies)
        assertEquals(
            Ticker(
                id = "eth",
                name = "Ethereum",
                symbol = "ETH",
                rank = 2,
                prices = mapOf(
                    CurrencySymbol.USD to expectedCurrency(price = 2500.0),
                ),
            ),
            ticker
        )
    }

    private fun tickerResponse(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
    ) = TickerResponse(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        totalSupply = 1_000L,
        maxSupply = 2_000L,
        betaValue = 1.0,
        firstDataAt = "2026-07-01T00:00:00Z",
        lastUpdated = "2026-07-01T00:00:00Z",
        quotes = mapOf(
            "BRL" to currencyResponse(price = 71420.0),
            "USD" to currencyResponse(price = 2500.0),
        ),
    )

    private fun currencyResponse(price: Double) = CurrencyResponse(
        price = price,
        volume24h = 100.0,
        volume24hChange24h = 1.5,
        marketCap = 1_000.0,
        marketCapChange24h = 2.5,
        percentChange15m = 0.1,
        percentChange30m = 0.2,
        percentChange1h = 0.3,
        percentChange6h = 0.4,
        percentChange12h = 0.5,
        percentChange24h = 0.6,
        percentChange7d = 0.7,
        percentChange30d = 0.8,
        percentChange1y = 0.9,
        athPrice = 3_000.0,
        athDate = "2026-01-01T00:00:00Z",
        percentFromPriceAth = -10.0,
    )

    private fun expectedCurrency(price: Double) = dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency(
        price = price,
        volume24h = 100.0,
        volume24hChange24h = 1.5,
        marketCap = dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap(
            marketCap = 1_000.0,
            lastChangeTwentyFourHours = 2.5,
        ),
        percentChangeInterval = dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval(
            p15m = 0.1,
            p30m = 0.2,
            p1h = 0.3,
            p6h = 0.4,
            p12h = 0.5,
            p24h = 0.6,
            p7d = 0.7,
            p30d = 0.8,
            p1y = 0.9,
        ),
        allTimeHigh = dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh(
            price = 3_000.0,
            date = "2026-01-01T00:00:00Z",
            percentage = -10.0,
        ),
    )

    private class FakeTickersDataSource(
        private val tickers: List<TickerResponse> = emptyList(),
        private val ticker: TickerResponse,
    ) : TickersDataSource {
        var requestedTickersCurrencies: List<String> = emptyList()
            private set
        var requestedTickerCoinId: String = ""
            private set
        var requestedTickerCurrencies: List<String> = emptyList()
            private set

        override suspend fun getTickers(currencies: List<String>): Flow<List<TickerResponse>> {
            requestedTickersCurrencies = currencies
            return flowOf(tickers)
        }

        override suspend fun getTicker(coinId: String, currencies: List<String>): Flow<TickerResponse> {
            requestedTickerCoinId = coinId
            requestedTickerCurrencies = currencies
            return flowOf(ticker)
        }
    }
}
