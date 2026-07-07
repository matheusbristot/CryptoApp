package dev.bristot.cryptoapp.presentation.recents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.R
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentTickersContainer(
    recentTickersController: RecentTickersController,
    showBackButton: Boolean = true,
    onBackButtonClick: () -> Unit,
    onSelectTicker: (Ticker) -> Unit,
) {
    val state by recentTickersController.state.collectAsStateWithLifecycle()
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recents",
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
        if (state.tickers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(innerPadding)
                    .testTag("recent_tickers_empty"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nenhum ticker recente",
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
                items(
                    items = state.tickers,
                    key = { ticker -> ticker.id },
                ) { ticker ->
                    TickerTile(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        ticker = ticker,
                        onClick = { _, _ ->
                            recentTickersController.addRecentTicker(ticker)
                            onSelectTicker(ticker)
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
