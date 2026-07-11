package dev.bristot.cryptoapp.feature.coins.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.theme.PrimaryPurple
import dev.bristot.cryptoapp.ui.theme.PrimaryPurpleDark

@Composable
fun CoinList(
    modifier: Modifier,
    lazyColumnRememberState: LazyListState,
    coins: List<Coin>,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            PrimaryPurple, PrimaryPurpleDark
        )
    )

    LazyColumn(
        state = lazyColumnRememberState,
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .padding(start = 24.dp, top = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        itemsIndexed(
            items = coins,
            key = { _: Int, coin: Coin -> coin.id },
        ) { index: Int, coin: Coin ->
            CoinListTile(
                modifier = Modifier.padding(bottom = if (index < coins.lastIndex) 12.dp else 0.dp),
                coin = coin,
            )
        }
    }
}
