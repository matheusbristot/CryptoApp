package dev.bristot.cryptoapp.presentation.ticker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.R
import dev.bristot.cryptoapp.presentation.tickers.TickerTile
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerContainer(
    modifier: Modifier = Modifier,
    name: String,
    tickerController: TickerController,
    showBackButton: Boolean = true,
    onBackButtonClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(modifier = Modifier, title = {
            Text(
                name,
                fontSize = 18.sp,
                color = textColors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }, navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = textColors.primary,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        })
    }) { innerPadding ->

        val state by tickerController.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = Unit) {
            tickerController.onLoadContent()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
        ) {

            if (state is TickerState.Error || state is TickerState.Loading) {
                item(key = "ticker-loading-single-component") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(
                                when (state) {
                                    is TickerState.Loading -> "ticker_loading"
                                    is TickerState.Error -> "ticker_error"
                                    else -> "ticker_content"
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state is TickerState.Loading) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = (state as TickerState.Error).error,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } else if (state is TickerState.Success) {
                val ticker = (state as TickerState.Success).ticker
                item(key = ticker.symbol) {
                    TickerTile(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        ticker = ticker,
                        onClick = { _, _ -> })
                }
            }
        }
    }
}
