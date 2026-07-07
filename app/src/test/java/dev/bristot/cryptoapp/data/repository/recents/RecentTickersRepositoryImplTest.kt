package dev.bristot.cryptoapp.data.repository.recents

import dev.bristot.cryptoapp.testutils.testTicker
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentTickersRepositoryImplTest {

    @Test
    fun addRecentTicker_addsTickerToTop() {
        val repository = RecentTickersRepositoryImpl()
        val bitcoin = testTicker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)

        repository.addRecentTicker(bitcoin)

        assertEquals(listOf(bitcoin), repository.observeRecentTickers().value)
    }

    @Test
    fun addRecentTicker_movesExistingTickerToTopWithoutDuplicating() {
        val repository = RecentTickersRepositoryImpl()
        val bitcoin = testTicker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val ethereum = testTicker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)

        repository.addRecentTicker(bitcoin)
        repository.addRecentTicker(ethereum)
        repository.addRecentTicker(bitcoin)

        assertEquals(listOf(bitcoin, ethereum), repository.observeRecentTickers().value)
    }

    @Test
    fun addRecentTicker_keepsAllTickersInAccessOrder() {
        val repository = RecentTickersRepositoryImpl()
        val bitcoin = testTicker(id = "btc", name = "Bitcoin", symbol = "BTC", rank = 1)
        val ethereum = testTicker(id = "eth", name = "Ethereum", symbol = "ETH", rank = 2)
        val solana = testTicker(id = "sol", name = "Solana", symbol = "SOL", rank = 3)
        val cardano = testTicker(id = "ada", name = "Cardano", symbol = "ADA", rank = 4)

        repository.addRecentTicker(bitcoin)
        repository.addRecentTicker(ethereum)
        repository.addRecentTicker(solana)
        repository.addRecentTicker(cardano)

        assertEquals(
            listOf(cardano, solana, ethereum, bitcoin),
            repository.observeRecentTickers().value,
        )
    }
}
