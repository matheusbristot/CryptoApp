package dev.bristot.cryptoapp.data.repository.coins

import dev.bristot.cryptoapp.data.datasource.coins.CoinsDatasource
import dev.bristot.cryptoapp.data.dto.coinDTO
import dev.bristot.cryptoapp.domain.entity.Coin
import dev.bristot.cryptoapp.domain.repository.CoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class CoinsRepositoryImpl(
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