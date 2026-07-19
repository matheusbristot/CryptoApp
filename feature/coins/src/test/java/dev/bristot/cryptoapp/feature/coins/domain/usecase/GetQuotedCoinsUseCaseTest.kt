package dev.bristot.cryptoapp.feature.coins.domain.usecase

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.AllTimeHigh
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Currency
import dev.bristot.cryptoapp.feature.tickers.domain.entity.MarketCap
import dev.bristot.cryptoapp.feature.tickers.domain.entity.PercentChangeInterval
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetQuotedCoinsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()

    @Test
    fun combinesCoinsWithSelectedQuoteAndCachesMetadata() = runTest(dispatcher) {
        val coinRepository = FakeCoinRepository(
            listOf(
                coin(id = "btc", rank = 1),
                coin(id = "eth", rank = 2),
            )
        )
        val tickersRepository = FakeTickersRepository(
            prices = mapOf(
                QuoteCurrency.BRL to 350_000.0,
                QuoteCurrency.USD to 65_000.0,
            )
        )
        val useCase = GetQuotedCoinsUseCase(
            dispatcherProvider = TestDispatcherProvider(dispatcher),
            coinRepository = coinRepository,
            tickersRepository = tickersRepository,
        )

        val brlCoins = useCase(QuoteCurrency.BRL)
        val usdCoins = useCase(QuoteCurrency.USD)

        assertEquals(350_000.0, brlCoins.first().quote?.price)
        assertEquals(QuoteCurrency.BRL, brlCoins.first().quote?.currency)
        assertEquals(65_000.0, usdCoins.first().quote?.price)
        assertEquals(QuoteCurrency.USD, usdCoins.first().quote?.currency)
        assertNull(usdCoins.last().quote?.price)
        assertEquals(1, coinRepository.requests)
        assertEquals(
            listOf(setOf(QuoteCurrency.BRL), setOf(QuoteCurrency.USD)),
            tickersRepository.requests,
        )
    }

    private fun coin(id: String, rank: Int) = Coin(
        id = id,
        name = id,
        symbol = id.uppercase(),
        rank = rank,
        isNew = false,
        isActive = true,
        type = "coin",
    )

    private class FakeCoinRepository(
        private val coins: List<Coin>,
    ) : CoinRepository {
        var requests: Int = 0
            private set

        override suspend fun getCoins(): Flow<List<Coin>> {
            requests++
            return flowOf(coins)
        }

        override fun observeCoin(coinId: String): Flow<Coin?> = flowOf(null)

        override suspend fun refreshCoin(coinId: String, force: Boolean) = Unit
    }

    private class FakeTickersRepository(
        private val prices: Map<QuoteCurrency, Double>,
    ) : TickersRepository {
        val requests = mutableListOf<Set<QuoteCurrency>>()

        override suspend fun getTickers(
            currencies: Set<QuoteCurrency>,
        ): Flow<List<Ticker>> {
            requests += currencies
            return flowOf(
                listOf(
                    Ticker(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        rank = 1,
                        prices = currencies.mapNotNull { currency ->
                            prices[currency]?.let { price -> currency to tickerCurrency(price) }
                        }.toMap(),
                    )
                )
            )
        }

        override suspend fun getTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker> = flowOf(
            Ticker(
                id = coinId,
                name = coinId,
                symbol = coinId.uppercase(),
                rank = 1,
                prices = emptyMap(),
            )
        )

        override fun observeTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
        ): Flow<Ticker?> = flowOf(null)

        override suspend fun refreshTicker(
            coinId: String,
            currencies: Set<QuoteCurrency>,
            force: Boolean,
        ) = Unit

        private fun tickerCurrency(price: Double) = Currency(
            price = price,
            volume24h = 0.0,
            volume24hChange24h = 0.0,
            marketCap = MarketCap(0.0, 0.0),
            percentChangeInterval = PercentChangeInterval(
                p15m = 0.0,
                p30m = 0.0,
                p1h = 0.0,
                p6h = 0.0,
                p12h = 0.0,
                p24h = 0.0,
                p7d = 0.0,
                p30d = 0.0,
                p1y = 0.0,
            ),
            allTimeHigh = AllTimeHigh(null, null, null),
        )
    }

    private class TestDispatcherProvider(
        override val main: CoroutineDispatcher,
    ) : DispatcherProvider {
        override val io: CoroutineDispatcher = main
        override val default: CoroutineDispatcher = main
    }
}
