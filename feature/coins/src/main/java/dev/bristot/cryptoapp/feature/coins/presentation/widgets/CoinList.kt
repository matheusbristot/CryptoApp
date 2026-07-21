package dev.bristot.cryptoapp.feature.coins.presentation.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.theme.PrimaryPurple
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@Composable
fun CoinList(
    modifier: Modifier,
    lazyColumnRememberState: LazyListState,
    coins: List<Coin>,
    valueFormatter: CryptoValueFormatter,
    onCoinClick: (Coin) -> Unit = {},
) {
    LazyColumn(
        state = lazyColumnRememberState,
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
    ) {
        item(key = "coins_header") {
            CoinListHeader(coins.size)
            Spacer(Modifier.height(24.dp))
        }
        itemsIndexed(items = coins, key = { _, coin -> coin.id }) { index, coin ->
            CoinListTile(
                coin = coin,
                valueFormatter = valueFormatter,
                onClick = { onCoinClick(coin) },
            )
            if (index < coins.lastIndex) Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CoinListHeader(coinCount: Int) {
    val dark = androidx.compose.foundation.isSystemInDarkTheme()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (dark) MaterialTheme.colorScheme.surface.copy(alpha = .45f) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (dark) Color(0xFF334155) else Color(0xFFCBD5E1)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = PrimaryPurple.copy(alpha = .12f)) {
                Icon(
                    Icons.Default.MonetizationOn,
                    null,
                    tint = PrimaryPurple,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "Cryptocurrencies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Explore the assets available on the market",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(shape = RoundedCornerShape(10.dp), color = PrimaryPurple.copy(alpha = .12f)) {
                Text(
                    "$coinCount ${if (coinCount == 1) "asset" else "assets"}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryPurple
                )
            }
        }
    }
}
