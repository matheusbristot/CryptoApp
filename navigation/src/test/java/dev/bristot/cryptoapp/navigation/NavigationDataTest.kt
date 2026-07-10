package dev.bristot.cryptoapp.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationDataTest {

    @Test
    fun `back stack starts with the initial destination`() {
        val navigationData = NavigationData(CryptoAppDestination.Tickers)

        assertEquals(listOf(CryptoAppDestination.Tickers), navigationData.backStack.toList())
        assertEquals(CryptoAppDestination.Tickers, navigationData.currentDestination)
        assertFalse(navigationData.hasStack())
    }

    @Test
    fun `forward and back update the stack`() {
        val navigationData = NavigationData(CryptoAppDestination.Tickers)

        navigationData.forward(CryptoAppDestination.TickerDetail(id = "1", name = "Bitcoin"))

        assertTrue(navigationData.hasStack())
        assertEquals(2, navigationData.backStack.size)

        navigationData.back()

        assertEquals(listOf(CryptoAppDestination.Tickers), navigationData.backStack.toList())
        assertFalse(navigationData.hasStack())
    }

    @Test
    fun `forward supports recent tickers destination`() {
        val navigationData = NavigationData(CryptoAppDestination.Tickers)

        navigationData.forward(CryptoAppDestination.RecentTickers)

        assertTrue(navigationData.hasStack())
        assertEquals(
            listOf(CryptoAppDestination.Tickers, CryptoAppDestination.RecentTickers),
            navigationData.backStack.toList(),
        )
        assertEquals(CryptoAppDestination.RecentTickers, navigationData.currentDestination)
    }
}
