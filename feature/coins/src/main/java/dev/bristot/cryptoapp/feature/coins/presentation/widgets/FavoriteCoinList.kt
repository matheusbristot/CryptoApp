package dev.bristot.cryptoapp.feature.coins.presentation.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.FavoriteCoinItem
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@Composable
fun FavoriteCoinList(
    modifier: Modifier,
    lazyListState: LazyListState,
    favorites: List<FavoriteCoinItem>,
    valueFormatter: CryptoValueFormatter,
    onCoinClick: (Coin) -> Unit,
    onUnavailableClick: (FavoriteCoinItem) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize().testTag("favorite_coin_list"),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(favorites, key = FavoriteCoinItem::id) { item ->
            val coin = item.coin
            if (coin != null) {
                CoinListTile(
                    coin = coin,
                    valueFormatter = valueFormatter,
                    onClick = { onCoinClick(coin) },
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("favorite_coin_loading_${item.id}"),
                    onClick = { onUnavailableClick(item) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(item.id, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Updating favorite data",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterEnd),
                        )
                    }
                }
            }
        }
    }
}
