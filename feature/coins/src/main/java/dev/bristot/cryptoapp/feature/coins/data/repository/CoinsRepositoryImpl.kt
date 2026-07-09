package dev.bristot.cryptoapp.feature.coins.data.repository

import dev.bristot.cryptoapp.feature.coins.data.datasource.CoinsDatasource
import dev.bristot.cryptoapp.feature.coins.data.dto.coinDTO
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinsRepositoryImpl @Inject constructor(
    private val coinsDatasource: CoinsDatasource,
) : CoinRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCoins(): Flow<List<Coin>> =
        coinsDatasource.getCoins().map { responseOfCoins ->
            responseOfCoins.filter { coinResponse -> coinResponse.isActive && coinResponse.type == "coin" }
                .map { coinRawValue ->
                    coinRawValue.coinDTO()
                }
        }
}
