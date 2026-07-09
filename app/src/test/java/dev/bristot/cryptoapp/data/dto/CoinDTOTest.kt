package dev.bristot.cryptoapp.data.dto

import dev.bristot.cryptoapp.data.model.CoinResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinDTOTest {

    @Test
    fun coinDTO_mapsCoinResponseToCoin() {
        val coin = CoinResponse(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            rank = 1,
            isNew = false,
            isActive = true,
            type = "coin",
        ).coinDTO()

        assertEquals("btc", coin.id)
        assertEquals("Bitcoin", coin.name)
        assertEquals("BTC", coin.symbol)
        assertEquals(1, coin.rank)
        assertEquals(true, coin.isActive)
    }
}
