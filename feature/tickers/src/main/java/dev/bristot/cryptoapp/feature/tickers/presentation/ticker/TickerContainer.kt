package dev.bristot.cryptoapp.feature.tickers.presentation.ticker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.feature.tickers.R
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.ui.theme.CryptoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerContainer(
    modifier: Modifier = Modifier,
    name: String,
    tickerController: TickerController,
    valueFormatter: CryptoValueFormatter,
    showBackButton: Boolean = true,
    onBackButtonClick: () -> Unit
) {
    val state by tickerController.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { tickerController.onLoadContent() }

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            Text(name, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }, navigationIcon = {
            if (showBackButton) IconButton(onClick = onBackButtonClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_button))
            }
        })
    }) { padding ->
        when (val current = state) {
            is TickerState.Initial -> StatusContent(true, "", Modifier.padding(padding))
            is TickerState.Loading -> StatusContent(true, "", Modifier.padding(padding))
            is TickerState.Error -> StatusContent(false, current.error, Modifier.padding(padding))
            is TickerState.Success -> TickerDetails(current.ticker, valueFormatter, Modifier.padding(padding))
        }
    }
}

@Composable
private fun StatusContent(loading: Boolean, error: String, modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().testTag(if (loading) "ticker_loading" else "ticker_error"),
        contentAlignment = Alignment.Center
    ) {
        if (loading) CircularProgressIndicator() else Text(error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun TickerDetails(ticker: Ticker, valueFormatter: CryptoValueFormatter, modifier: Modifier = Modifier) {
    val quoteEntry = ticker.prices.entries.first()
    val symbol = quoteEntry.key
    val quote = quoteEntry.value
    val changes = quote.percentChangeInterval.run {
        listOf("15m" to p15m, "30m" to p30m, "1h" to p1h, "6h" to p6h, "12h" to p12h,
            "24h" to p24h, "7d" to p7d, "30d" to p30d, "1y" to p1y)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("ticker_details"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${ticker.symbol} · #${ticker.rank}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.ticker_updated, valueFormatter.date(ticker.lastUpdated)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(stringResource(R.string.ticker_price), style = MaterialTheme.typography.labelLarge)
                Text(valueFormatter.currency(quote.price, symbol.name), style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, modifier = Modifier.testTag("ticker_price"))
                ChangeText("24h", quote.percentChangeInterval.p24h, valueFormatter)
            }
        }
        item { SectionTitle(stringResource(R.string.ticker_variations)) }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                changes.forEach { (label, value) -> ChangeChip(label, value, valueFormatter) }
            }
        }
        item { SectionTitle(stringResource(R.string.ticker_market)) }
        item { MetricRow(stringResource(R.string.ticker_market_cap), valueFormatter.currency(quote.marketCap.marketCap, symbol.name)) }
        item { MetricRow(stringResource(R.string.ticker_market_cap_change), valueFormatter.percentage(quote.marketCap.lastChangeTwentyFourHours)) }
        item { MetricRow(stringResource(R.string.ticker_volume), valueFormatter.currency(quote.volume24h, symbol.name)) }
        item { MetricRow(stringResource(R.string.ticker_volume_change), valueFormatter.percentage(quote.volume24hChange24h)) }
        item { SectionTitle(stringResource(R.string.ticker_supply)) }
        item { MetricRow(stringResource(R.string.ticker_total_supply), valueFormatter.integer(ticker.totalSupply)) }
        item { MetricRow(stringResource(R.string.ticker_max_supply), valueFormatter.integer(ticker.maxSupply)) }
        item { MetricRow(stringResource(R.string.ticker_beta), valueFormatter.decimal(ticker.betaValue)) }
        item { SectionTitle(stringResource(R.string.ticker_ath)) }
        item { MetricRow(stringResource(R.string.ticker_ath), valueFormatter.currency(quote.allTimeHigh.price, symbol.name)) }
        item { MetricRow(stringResource(R.string.ticker_distance_ath), valueFormatter.percentage(quote.allTimeHigh.percentage)) }
        item { MetricRow(stringResource(R.string.ticker_ath_date), valueFormatter.date(quote.allTimeHigh.date)) }
    }
}

@Composable private fun SectionTitle(text: String) = Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

@Composable
private fun MetricRow(label: String, value: String) {
    Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ChangeChip(label: String, value: Double, valueFormatter: CryptoValueFormatter) {
    Surface(shape = RoundedCornerShape(12.dp), color = changeColor(value).copy(alpha = .12f)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(valueFormatter.percentage(value), color = changeColor(value), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun ChangeText(label: String, value: Double, valueFormatter: CryptoValueFormatter) =
    Text("$label  ${valueFormatter.percentage(value)}", color = changeColor(value), fontWeight = FontWeight.Bold)

private fun changeColor(value: Double): Color = if (value >= 0) CryptoTheme.Positive else CryptoTheme.Negative
