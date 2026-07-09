package dev.bristot.cryptoapp.feature.coins.data.repository

import dev.bristot.cryptoapp.feature.coins.data.datasource.CoinsDatasource
import dev.bristot.cryptoapp.feature.coins.data.model.CoinResponse
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinsRepositoryImplTest {

    @Test
    fun getCoins_filtersInactiveAndNonCoinItemsAndMapsToDomain() = runBlocking {
        val dataSource = FakeCoinsDatasource(
            coins = listOf(
                coin(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1, isActive = true, type = "coin"),
                coin(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2, isActive = false, type = "coin"),
                coin(id = "usd", name = "USD Coin", symbol = "USDC", rank = 3, isActive = true, type = "token"),
            )
        )
        val repository = CoinsRepositoryImpl(coinsDatasource = dataSource)

        val coins = repository.getCoins().first()

        assertEquals(
            listOf(
                Coin(
                    id = "btc",
                    name = "Bitcoin",
                    symbol = "BTC",
                    rank = 1,
                    isNew = false,
                    isActive = true,
                    type = "coin",
                )
            ),
            coins
        )
    }

    private fun coin(
        id: String,
        name: String,
        symbol: String,
        rank: Int,
        isActive: Boolean,
        type: String,
    ) = CoinResponse(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        isNew = false,
        isActive = isActive,
        type = type,
    )

    private class FakeCoinsDatasource(
        private val coins: List<CoinResponse>,
    ) : CoinsDatasource {
        override suspend fun getCoins(): Flow<List<CoinResponse>> = flowOf(coins)
    }
}
