package dev.bristot.cryptoapp.feature.tickers.data.datasource.tickers

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.tickers.data.api.tickers.TickersRoutes
import dev.bristot.cryptoapp.feature.tickers.data.model.TickerResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TickersRemoteDataSourceImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val logger: CryptoLogger,
    private val tickersRoutes: TickersRoutes
) : TickersDataSource {

    override suspend fun getTickers(currencies: List<String>): Flow<List<TickerResponse>> = flow {
        try {
            logger.debug(message = "Thread: ${Thread.currentThread().name}")
            val tickers = tickersRoutes.getTickersByQuotes(
                quotes = currencies.joinToString(separator = ",")
            )
            logger.debug(message = "Thread: ${Thread.currentThread().name}")
            emit(tickers)
        } catch (exception: Exception) {
            throw exception
        }
    }.flowOn(dispatcherProvider.io)

    override suspend fun getTicker(coinId: String, currencies: List<String>): Flow<TickerResponse> =
        flow {
            try {
                logger.debug(message = "Thread: ${Thread.currentThread().name}")
                val ticker = tickersRoutes.getTickerByQuotes(
                    coinId = coinId,
                    quotes = currencies.joinToString(separator = ","),
                )
                logger.debug(message = "Thread: ${Thread.currentThread().name}")
                emit(ticker)
            } catch (exception: Exception) {
                throw exception
            }
        }.flowOn(dispatcherProvider.io)
}
