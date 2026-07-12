package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

@TraceRecomposition
@Composable
fun TickerTile(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    textColor: Color,
    secondaryTextColor: Color,
    ticker: Ticker,
    valueFormatter: CryptoValueFormatter,
    onClick: (id: String, name: String) -> Unit,
) {
    val (currencySymbol, quote) = ticker.prices.entries.first()
    val change = quote.percentChangeInterval.p24h
    val positive = change >= 0
    val changeColor = if (positive) CryptoTheme.Positive else CryptoTheme.Negative
    val cardColor = if (isDarkMode) CryptoTheme.CardDark.copy(alpha = .55f) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF293548) else Color(0xFFE2E8F0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(ticker.id, ticker.name) }
            .testTag("ticker_tile_${ticker.id}"),
        shape = RoundedCornerShape(18.dp),
        color = cardColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isDarkMode) 0.dp else 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = ticker.symbol.take(1),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(start = 12.dp).weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        ticker.name,
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${ticker.symbol} · ${currencySymbol.name}",
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        text = "#${ticker.rank}",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Preço", color = secondaryTextColor, style = MaterialTheme.typography.labelSmall)
                    Text(
                        valueFormatter.currency(quote.price, currencySymbol.name),
                        color = textColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ticker_price"),
                    )
                    Text(
                        "Market cap ${valueFormatter.compactCurrency(quote.marketCap.marketCap, currencySymbol.name)}",
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                VariableCoin(
                    change = change,
                    changeColor = changeColor,
                    isPositive = positive,
                    valueFormatter = valueFormatter,
                )
            }
        }
    }
}

@Composable
fun VariableCoin(
    modifier: Modifier = Modifier,
    change: Double,
    changeColor: Color,
    isPositive: Boolean,
    valueFormatter: CryptoValueFormatter,
) {
    Surface(shape = RoundedCornerShape(10.dp), color = changeColor.copy(alpha = .12f)) {
        Row(
            modifier = modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = changeColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = valueFormatter.percentage(change),
                color = changeColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("ticker_change"),
            )
            Text("24h", color = changeColor, style = MaterialTheme.typography.labelSmall)
        }
    }
}
