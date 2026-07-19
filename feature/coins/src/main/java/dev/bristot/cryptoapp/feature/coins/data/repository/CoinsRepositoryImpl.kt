package dev.bristot.cryptoapp.feature.coins.data.repository

import dev.bristot.cryptoapp.feature.coins.data.datasource.CoinsDatasource
import dev.bristot.cryptoapp.feature.coins.data.dto.coinDTO
import dev.bristot.cryptoapp.feature.coins.data.local.CoinsLocalDataSource
import dev.bristot.cryptoapp.feature.coins.data.local.toCacheEntity
import dev.bristot.cryptoapp.feature.coins.data.local.toDomain
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import dev.bristot.cryptoapp.time.TimeProvider
import javax.inject.Inject

class CoinsRepositoryImpl @Inject constructor(
    private val coinsDatasource: CoinsDatasource,
    private val localDataSource: CoinsLocalDataSource,
    private val timeProvider: TimeProvider,
) : CoinRepository {

    private val refreshMutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCoins(): Flow<List<Coin>> =
        coinsDatasource.getCoins().map { responseOfCoins ->
            responseOfCoins.filter { coinResponse -> coinResponse.isActive && coinResponse.type == "coin" }
                .map { coinRawValue ->
                    coinRawValue.coinDTO()
                }
        }

    override fun observeCoin(coinId: String): Flow<Coin?> =
        localDataSource.observeCoin(coinId).map { entity -> entity?.toDomain() }

    override suspend fun refreshCoin(coinId: String, force: Boolean) {
        refreshMutex.withLock {
            val now = timeProvider.currentTimeMillis()
            val cached = localDataSource.getCoin(coinId)
            val isFresh = cached?.let { entity ->
                val age = now - entity.fetchedAtEpochMillis
                age in 0 until COIN_FRESHNESS_MILLIS
            } ?: false
            if (!force && isFresh) return

            val response = coinsDatasource.getCoin(coinId)
            localDataSource.upsertCoin(response.toCacheEntity(fetchedAtEpochMillis = now))
        }
    }

    private companion object {
        const val COIN_FRESHNESS_MILLIS = 60_000L
    }
}
