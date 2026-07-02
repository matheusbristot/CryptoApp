package dev.bristot.cryptoapp.presentation.tickers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.skydoves.compose.stability.runtime.TraceRecomposition
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

@TraceRecomposition
@Composable
fun TickerTile(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    textColor: Color,
    secondaryTextColor: Color,
    ticker: Ticker,
    onClick: (id: String, name: String) -> Unit,
) {
    val price = ticker.prices.values.first()
    val isPositive = price.volume24hChange24h > 0
    val cardColor = if (isDarkMode) CryptoTheme.CardDark.copy(alpha = 0.4f) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF293548) else Color(0xFFE2E8F0)
    val changeColor =
        if (price.volume24hChange24h > 0) CryptoTheme.Positive else CryptoTheme.Negative


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(ticker.id, ticker.name) }
            .testTag("ticker_tile_${ticker.id}"),
        color = cardColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticker.rank.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .width(16.dp)
                        .weight(0.25f)
                )

                Column(
                    modifier = Modifier.weight(0.75f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = ticker.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = ticker.symbol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryTextColor
                    )
                }
            }

            VariableCoin(
                price = price.price.toString(),
                textColor = textColor,
                changeColor = changeColor,
                volumeChange = price.volume24hChange24h.toString(),
                isPositive = isPositive,
            )
        }
    }
}


@Composable
fun VariableCoin(
    modifier: Modifier = Modifier,
    price: String,
    textColor: Color,
    changeColor: Color,
    volumeChange: String,
    isPositive: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.testTag("ticker_price")
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPositive) Icons.AutoMirrored.Default.TrendingUp
                else Icons.AutoMirrored.Default.TrendingDown,
                contentDescription = null,
                tint = changeColor,
                modifier = modifier.size(16.sp.value.dp)
            )
            Text(
                text = "${volumeChange}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = changeColor,
                modifier = Modifier.testTag("ticker_change")
            )
        }
    }
}
