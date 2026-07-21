package dev.bristot.cryptoapp.feature.coins.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailComponent(
    name: String,
    controller: CoinDetailController,
    valueFormatter: CryptoValueFormatter,
    showBackButton: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by controller.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.coin?.name ?: name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = controller.toggleFavorite,
                        modifier = Modifier.testTag("coin_favorite_button"),
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (state.isFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            },
                            tint = if (state.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                },
            )
        },
    ) { padding ->
        val coin = state.coin
        when {
            coin != null -> CoinDetails(
                coin = coin,
                valueFormatter = valueFormatter,
                refreshError = state.errorMessage,
                modifier = Modifier.padding(padding),
            )
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).testTag("coin_detail_loading"),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            else -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).testTag("coin_detail_error"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.errorMessage ?: "Data unavailable",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
    }
}

@Composable
private fun CoinDetails(
    coin: Coin,
    valueFormatter: CryptoValueFormatter,
    refreshError: String?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("coin_details"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "${coin.symbol.uppercase()} · #${coin.rank}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text("Price", style = MaterialTheme.typography.labelLarge)
                    Text(
                        coin.quote?.let { quote ->
                            quote.price?.let { price ->
                                valueFormatter.currency(price, quote.currency.name)
                            }
                        } ?: "Price unavailable",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("coin_detail_price"),
                    )
                }
            }
        }
        if (refreshError != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().testTag("coin_detail_partial_error"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        refreshError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp),
                    )
                }
            }
        }
        item { DetailMetric("Identifier", coin.id) }
        item { DetailMetric("Type", coin.type) }
        item { DetailMetric("Status", if (coin.isActive) "Active" else "Inactive") }
        item { DetailMetric("New asset", if (coin.isNew) "Yes" else "No") }
    }
}

@Composable
private fun DetailMetric(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}
