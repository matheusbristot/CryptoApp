package dev.bristot.cryptoapp.data.datasource.coins

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.data.api.coins.CoinsRoutes
import dev.bristot.cryptoapp.data.model.CoinResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class CoinsRemoteDataSourceImpl @Inject constructor(
    private val coinsRoutes: CoinsRoutes,
    private val logger: CryptoLogger,
    private val dispatcherProvider: DispatcherProvider
) : CoinsDatasource {

    override suspend fun getCoins(): Flow<List<CoinResponse>> = flow {
        try {
            logger.debug(message = "Thread: ${Thread.currentThread().name}")
            val coins = coinsRoutes.getCoins()
            logger.debug(message = "Thread: ${Thread.currentThread().name}")
            emit(coins)
        } catch (exception: Exception) {
            throw exception
        }
    }.flowOn(dispatcherProvider.io)

}
