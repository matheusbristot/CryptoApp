package dev.bristot.cryptoapp.feature.tickers.presentation.recents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.feature.tickers.R
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentTickersContainer(
    recentTickersController: RecentTickersController,
    showBackButton: Boolean = true,
    onBackButtonClick: () -> Unit,
    onSelectTicker: (Ticker) -> Unit,
    valueFormatter: CryptoValueFormatter,
    quoteCurrency: QuoteCurrency = QuoteCurrency.BRL,
) {
    val state by recentTickersController.state.collectAsStateWithLifecycle()
    val visibleTickers = state.tickers.filter { quoteCurrency in it.prices }
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.recent_tickers_title),
                        color = textColors.primary,
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackButtonClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                tint = textColors.primary,
                                contentDescription = stringResource(R.string.back_button),
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding: PaddingValues ->
        if (visibleTickers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(innerPadding)
                    .testTag("recent_tickers_empty"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.recent_tickers_empty),
                    color = textColors.secondary,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(innerPadding)
                    .testTag("recent_tickers_list"),
            ) {
                item(key = "recent_header") {
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        Text(
                            text = stringResource(R.string.recent_tickers_title),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColors.primary,
                        )
                        Text(
                            text = stringResource(R.string.recent_tickers_subtitle),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = textColors.secondary,
                        )
                    }
                }
                items(
                    items = visibleTickers,
                    key = { ticker -> ticker.id },
                ) { ticker ->
                    TickerTile(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        ticker = ticker,
                        valueFormatter = valueFormatter,
                        onClick = { _, _ ->
                            recentTickersController.addRecentTicker(ticker)
                            onSelectTicker(ticker)
                        },
                        quoteCurrency = quoteCurrency,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
