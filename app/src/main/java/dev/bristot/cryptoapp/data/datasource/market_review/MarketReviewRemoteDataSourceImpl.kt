package dev.bristot.cryptoapp.data.datasource.market_review

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.data.api.global.GlobalRoutes
import dev.bristot.cryptoapp.data.model.MarketReviewResponse
import dev.bristot.cryptoapp.logger.CryptoLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MarketReviewRemoteDataSourceImpl @Inject constructor(
    private val logger: CryptoLogger,
    private val dispatcherProvider: DispatcherProvider,
    private val globalRoutes: GlobalRoutes,
) : MarketReviewDataSource {
    override fun getMarketOverviewData(): Flow<MarketReviewResponse> = flow {
        try {
            val coins = globalRoutes.getMarketReviewData()
            logger.debug(message = "Thread: ${Thread.currentThread().name}")
            emit(coins)
        } catch (exception: Exception) {
            throw exception
        }
    }.flowOn(dispatcherProvider.io)
}