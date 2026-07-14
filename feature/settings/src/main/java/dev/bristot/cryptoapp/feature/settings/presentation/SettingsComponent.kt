package dev.bristot.cryptoapp.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.feature.settings.R
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.settings.api.AppSettings.Companion.MAX_REQUESTED_QUOTES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComponent(
    controller: SettingsController,
) {
    val settings by controller.settings.collectAsStateWithLifecycle()
    val enabledCurrencies = settings.requestedQuoteCurrencies.sortedBy(QuoteCurrency::name)
    val selectionLimitReached = enabledCurrencies.size >= MAX_REQUESTED_QUOTES

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                SettingsSection(
                    title = stringResource(R.string.settings_display_quote_title),
                    description = stringResource(R.string.settings_display_quote_description),
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        enabledCurrencies.forEach { currency ->
                            FilterChip(
                                selected = settings.selectedQuoteCurrency == currency,
                                onClick = { controller.selectQuote(currency) },
                                label = { Text(currency.name) },
                            )
                        }
                    }
                }
                HorizontalDivider()
                SettingsSection(
                    title = stringResource(R.string.settings_requested_quotes_title),
                    description = stringResource(R.string.settings_requested_quotes_description),
                )
            }
            items(
                items = QuoteCurrency.entries,
                key = QuoteCurrency::name,
            ) { currency ->
                val checked = currency in settings.requestedQuoteCurrencies
                val isOnlyCurrency = checked && settings.requestedQuoteCurrencies.size == 1
                ListItem(
                    headlineContent = { Text(currency.name) },
                    supportingContent = {
                        if (currency == settings.selectedQuoteCurrency) {
                            Text(stringResource(R.string.settings_current_quote))
                        }
                    },
                    trailingContent = {
                        Checkbox(
                            checked = checked,
                            enabled = !isOnlyCurrency && (checked || !selectionLimitReached),
                            onCheckedChange = { controller.setQuoteEnabled(currency, it) },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    content: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content?.invoke()
    }
}
