package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

const val RECENT_TICKERS_PREVIEW_LIMIT = 3

@Composable
fun RecentTickersSection(
    tickers: List<Ticker>,
    isDarkMode: Boolean,
    textColors: AppTextColors,
    onTitleClick: () -> Unit,
    onTickerClick: (Ticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleTickers = tickers.take(RECENT_TICKERS_PREVIEW_LIMIT)
    if (visibleTickers.isEmpty()) return

    val containerColor = if (isDarkMode) CryptoTheme.CardDark.copy(alpha = 0.25f) else Color(0xFFF8FAFC)
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("recent_tickers_section"),
        color = containerColor,
        border = BorderStroke(width = 1.dp, color = borderColor),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onTitleClick)
                    .padding(vertical = 4.dp)
                    .testTag("recent_tickers_title"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Recents",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColors.primary,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = textColors.secondary,
                )
            }

            visibleTickers.forEachIndexed { index, ticker ->
                TickerTile(
                    isDarkMode = isDarkMode,
                    textColor = textColors.primary,
                    secondaryTextColor = textColors.secondary,
                    ticker = ticker,
                    onClick = { _, _ -> onTickerClick(ticker) },
                )
                if (index < visibleTickers.lastIndex) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}
