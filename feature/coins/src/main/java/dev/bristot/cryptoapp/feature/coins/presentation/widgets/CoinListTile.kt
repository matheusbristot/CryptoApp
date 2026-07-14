package dev.bristot.cryptoapp.feature.coins.presentation.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.theme.PrimaryPurple
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import androidx.compose.ui.res.stringResource
import dev.bristot.cryptoapp.feature.coins.R

@Composable
fun CoinListTile(
    modifier: Modifier = Modifier,
    coin: Coin,
    valueFormatter: CryptoValueFormatter,
) {
    val dark = androidx.compose.foundation.isSystemInDarkTheme()
    Surface(
        modifier = modifier.fillMaxWidth().testTag("coin_tile_${coin.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (dark) Color(0xFF293548) else Color(0xFFE2E8F0)),
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(CircleShape).background(PrimaryPurple.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
                Text(coin.symbol.firstOrNull()?.uppercase() ?: "?", color = PrimaryPurple, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(coin.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val quoteText = coin.quote?.let { quote ->
                    quote.price?.let { price ->
                        valueFormatter.currency(price, quote.currency.name)
                    } ?: stringResource(R.string.coin_price_unavailable, quote.currency.name)
                } ?: stringResource(R.string.coin_price_not_loaded)
                Text(
                    "${coin.symbol.uppercase()} · $quoteText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .6f)) {
                Text("#${coin.rank}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
