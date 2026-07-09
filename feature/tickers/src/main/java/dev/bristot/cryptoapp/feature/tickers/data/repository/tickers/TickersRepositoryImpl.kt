package dev.bristot.cryptoapp.feature.tickers.data.repository.tickers

import dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers.TickersDataSource
import dev.bristot.cryptoapp.feature.tickers.data.dto.toTicker
import dev.bristot.cryptoapp.feature.tickers.data.dto.toTickers
import dev.bristot.cryptoapp.feature.tickers.domain.entity.CurrencySymbol
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TickersRepositoryImpl @Inject constructor(
    private val tickersDataSource: TickersDataSource
) : TickersRepository {

    override suspend fun getTickers(currencies: Set<CurrencySymbol>): Flow<List<Ticker>> {
        val currenciesNamed = currencies.map { symbol -> symbol.name }
        return tickersDataSource.getTickers(currencies = currenciesNamed)
            .map { tickerResponse -> tickerResponse.toTickers(currenciesOf = currencies) }
    }

    override suspend fun getTicker(coinId: String, currencies: Set<CurrencySymbol>): Flow<Ticker> {
        val currenciesNamed = currencies.map { symbol -> symbol.name }
        return tickersDataSource.getTicker(coinId = coinId, currencies = currenciesNamed)
            .map { tickerResponse ->
                tickerResponse.toTicker(currenciesOf = currencies)
            }
    }
}
