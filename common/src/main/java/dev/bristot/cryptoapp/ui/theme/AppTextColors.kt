package dev.bristot.cryptoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Immutable
data class AppTextColors(
    val primary: Color,
    val secondary: Color,
)

fun resolveAppTextColors(isDarkMode: Boolean): AppTextColors {
    return if (isDarkMode) {
        AppTextColors(
            primary = Color.White,
            secondary = Color(0xFFCBD5E1),
        )
    } else {
        AppTextColors(
            primary = Color(0xFF1F2937),
            secondary = Color(0xFF64748B),
        )
    }
}

@Composable
fun rememberAppTextColors(isDarkMode: Boolean = isSystemInDarkTheme()): AppTextColors {
    return remember(isDarkMode) {
        resolveAppTextColors(isDarkMode)
    }
}
