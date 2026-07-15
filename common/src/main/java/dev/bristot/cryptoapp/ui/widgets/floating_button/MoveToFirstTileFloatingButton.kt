package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

val LocalScrollToTop = staticCompositionLocalOf<() -> Unit> { {} }
val LocalBottomBarPadding = compositionLocalOf { 0.dp }

@Composable
fun MoveToFirstTileFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val onScrollToTop = LocalScrollToTop.current
    val bottomBarPadding = LocalBottomBarPadding.current

    Box(
        modifier = modifier
            .padding(bottom = bottomBarPadding)
            .background(color = Color.White)
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Color(0xFFE2E8F0)))
            .clickable {
                onScrollToTop()
                onClick()
            }
            .testTag("move_to_first_tile_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
        )
    }
}
