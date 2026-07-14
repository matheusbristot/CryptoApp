package dev.bristot.cryptoapp.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationDataTest {

    private data object InitialDestination : RootDestination
    private data object DetailDestination : CryptoAppDestination

    @Test
    fun `back stack starts with the initial destination`() {
        val navigationData = NavigationData(InitialDestination)

        assertEquals(listOf(InitialDestination), navigationData.backStack.toList())
        assertEquals(InitialDestination, navigationData.currentDestination)
        assertFalse(navigationData.hasStack())
    }

    @Test
    fun `forward and back update the stack`() {
        val navigationData = NavigationData(InitialDestination)

        navigationData.forward(DetailDestination)

        assertTrue(navigationData.hasStack())
        assertEquals(2, navigationData.backStack.size)

        navigationData.back()

        assertEquals(listOf(InitialDestination), navigationData.backStack.toList())
        assertFalse(navigationData.hasStack())
    }

    @Test
    fun `forward supports feature destination`() {
        val navigationData = NavigationData(InitialDestination)

        navigationData.forward(DetailDestination)

        assertTrue(navigationData.hasStack())
        assertEquals(
            listOf(InitialDestination, DetailDestination),
            navigationData.backStack.toList(),
        )
        assertEquals(DetailDestination, navigationData.currentDestination)
    }
}
