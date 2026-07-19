package dev.bristot.cryptoapp.feature.coins.data.datasource

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.data.api.CoinsRoutes
import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CoinsRemoteDataSourceImpl @Inject constructor(
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

    override suspend fun getCoin(coinId: String): CoinResponse =
        coinsRoutes.getCoinById(coinId = coinId)

}
