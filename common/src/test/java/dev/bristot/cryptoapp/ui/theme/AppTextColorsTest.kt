package dev.bristot.cryptoapp.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class AppTextColorsTest {

    @Test
    fun `resolveAppTextColors returns dark palette when dark mode is enabled`() {
        val colors = resolveAppTextColors(isDarkMode = true)

        assertEquals(Color.White, colors.primary)
        assertEquals(Color(0xFFCBD5E1), colors.secondary)
    }

    @Test
    fun `resolveAppTextColors returns light palette when dark mode is disabled`() {
        val colors = resolveAppTextColors(isDarkMode = false)

        assertEquals(Color(0xFF1F2937), colors.primary)
        assertEquals(Color(0xFF64748B), colors.secondary)
    }
}
