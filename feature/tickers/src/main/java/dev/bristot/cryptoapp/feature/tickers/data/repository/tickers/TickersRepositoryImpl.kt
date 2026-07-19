package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers.TickersDataSource
import dev.bristot.cryptoapp.feature.tickers.data.dto.toTicker
import dev.bristot.cryptoapp.feature.tickers.data.dto.toTickers
import dev.bristot.cryptoapp.feature.tickers.data.local.TickersLocalDataSource
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import dev.bristot.cryptoapp.time.TimeProvider
import javax.inject.Inject

class TickersRepositoryImpl @Inject constructor(
    private val tickersDataSource: TickersDataSource,
    private val localDataSource: TickersLocalDataSource,
    private val timeProvider: TimeProvider,
) : TickersRepository {

    private val refreshMutex = Mutex()

    override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> {
        val currenciesNamed = currencies.map { symbol -> symbol.name }
        return tickersDataSource.getTickers(currencies = currenciesNamed)
            .map { tickerResponse -> tickerResponse.toTickers(currenciesOf = currencies) }
    }

    override suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker> {
        validateCurrencies(currencies)
        return flow {
            val quotesKey = currencies.quotesKey()
            val cached = localDataSource.getTicker(coinId, quotesKey)
            if (cached != null) {
                emit(cached.response.toTicker(currenciesOf = currencies))
            }

            try {
                refreshTicker(coinId = coinId, currencies = currencies)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (cached == null) throw exception
            }

            emitAll(observeTicker(coinId, currencies).filterNotNull())
        }.distinctUntilChanged()
    }

    override fun observeTicker(
        coinId: String,
        currencies: Set<CurrencySymbol>,
    ): Flow<Ticker?> {
        validateCurrencies(currencies)
        return localDataSource.observeTicker(coinId, currencies.quotesKey())
            .map { cached -> cached?.response?.toTicker(currenciesOf = currencies) }
    }

    override suspend fun refreshTicker(
        coinId: String,
        currencies: Set<CurrencySymbol>,
        force: Boolean,
    ) {
        validateCurrencies(currencies)
        refreshMutex.withLock {
            val quotesKey = currencies.quotesKey()
            val now = timeProvider.currentTimeMillis()
            val cached = localDataSource.getTicker(coinId, quotesKey)
            val isFresh = cached?.let { ticker ->
                val age = now - ticker.fetchedAtEpochMillis
                age in 0 until TICKER_FRESHNESS_MILLIS
            } ?: false
            if (!force && isFresh) return

            val currenciesNamed = currencies.map { symbol -> symbol.name }
            val response = tickersDataSource.getTicker(
                coinId = coinId,
                currencies = currenciesNamed,
            ).first()
            localDataSource.upsertTicker(
                response = response,
                quotesKey = quotesKey,
                fetchedAtEpochMillis = now,
            )
        }
    }

    private fun validateCurrencies(currencies: Set<CurrencySymbol>) {
        require(currencies.isNotEmpty()) { "At least one quote currency is required" }
        require(currencies.size <= 3) { "At most three quote currencies are supported" }
    }

    private fun Set<CurrencySymbol>.quotesKey(): String =
        map { currency -> currency.name }.sorted().joinToString(separator = ",")

    private companion object {
        const val TICKER_FRESHNESS_MILLIS = 5 * 60_000L
    }
}
