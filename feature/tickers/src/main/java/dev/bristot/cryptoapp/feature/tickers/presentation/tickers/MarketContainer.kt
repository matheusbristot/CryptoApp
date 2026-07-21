package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RECENT_TICKERS_PREVIEW_LIMIT
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersSection
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import dev.bristot.cryptoapp.ui.sort.SortComponent
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewQuoteData
import dev.bristot.cryptoapp.feature.tickers.R

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
fun MarketContainer(
    tickersController: TickersController,
    recentTickersController: RecentTickersController,
    floatingButtonController: FloatingButtonController,
    marketOverviewHeaderContent: @Composable (
        isDarkMode: Boolean,
        textColors: AppTextColors,
        quoteData: MarketOverviewQuoteData?,
    ) -> Unit,
    sortController: SortController,
    onOpenRecentTickers: () -> Unit,
    onSelectTicker: (Ticker) -> Unit,
    onSelectFavorite: (id: String, name: String) -> Unit = { _, _ -> },
    valueFormatter: CryptoValueFormatter,
    quoteCurrency: QuoteCurrency = QuoteCurrency.BRL,
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)
    val floatingState by floatingButtonController.state.collectAsStateWithLifecycle()
    val tickersState by tickersController.state.collectAsStateWithLifecycle()
    val favoritesState by tickersController.favoritesState.collectAsStateWithLifecycle()
    val selectedSection by tickersController.selectedSection.collectAsStateWithLifecycle()
    val recentTickersState by recentTickersController.state.collectAsStateWithLifecycle()
    val sortState by sortController.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val indexes = rememberSaveable(floatingState.listState) {
        floatingState.listState.firstVisibleItemIndex to floatingState.listState.firstVisibleItemScrollOffset
    }
    val lazyColumnRememberState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = indexes.first,
        initialFirstVisibleItemScrollOffset = indexes.second
    )

    val contentData = remember(tickersState, recentTickersState, quoteCurrency) {

        var tickers = emptyList<Ticker>()
        if ((tickersState as? TickersState.Success)?.tickers != null) {
            tickers = (tickersState as TickersState.Success).tickers
                .filter { quoteCurrency in it.prices }
        }

        val recentTickers = recentTickersState.tickers
            .filter { quoteCurrency in it.prices }
            .take(RECENT_TICKERS_PREVIEW_LIMIT)
        val recentTickerIds = recentTickers.map { ticker -> ticker.id }.toSet()
        val listedTickers = tickers.filterNot { ticker -> ticker.id in recentTickerIds }

        MarketContainerData.HasContent(
            tickersData = listedTickers,
            recentTickersData = recentTickers,
            isTickersLoading = tickersState is TickersState.Initial || tickersState is TickersState.Loading,
            tickerError = (tickersState as? TickersState.Error)?.error,
        )
    }

    val marketOverviewQuoteData = remember(tickersState, quoteCurrency) {
        (tickersState as? TickersState.Success)?.tickers
            ?.mapNotNull { ticker -> ticker.prices[quoteCurrency] }
            ?.let { quotes ->
                MarketOverviewQuoteData(
                    currencyCode = quoteCurrency.name,
                    marketCap = quotes.sumOf { quote -> quote.marketCap.marketCap },
                    volume24h = quotes.sumOf { quote -> quote.volume24h },
                )
            }
    }

    val currentContentData by rememberUpdatedState(contentData)
    val displayedTickers = if (selectedSection == TickersSection.FAVORITES) {
        favoritesState.tickers
    } else {
        currentContentData.tickersData
    }

    LaunchedEffect(lazyColumnRememberState) {
        snapshotFlow {
            Pair(
                lazyColumnRememberState.firstVisibleItemIndex,
                lazyColumnRememberState.firstVisibleItemScrollOffset
            )
        }.collectLatest { (index, offset) ->
            val shouldShowButton = index > 5
            floatingButtonController.onHandleVisibility(shouldShowButton)
            floatingButtonController.onSaveScroll(index, offset)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier,
                    title = {
                        Text("CryptoApp")
                    },
                    actions = {
                        AnimatedVisibility(visible = displayedTickers.isNotEmpty()) {
                            SortComponent(
                                state = sortState,
                                onScrollToFirstIndex = {
                                    coroutineScope.launch {
                                        lazyColumnRememberState.scrollToItem(index = 0)
                                    }
                                },
                                onChangeType = { type ->
                                    val updated = sortState.copy(type = type)
                                    sortController.changeType(type)
                                    tickersController.sortBy(updated)
                                },
                                onChangeOrder = { order ->
                                    val updated = sortState.copy(order = order)
                                    sortController.changeOrder(order)
                                    tickersController.sortBy(updated)
                                },
                            )
                        }
                    },
                )
                if (favoritesState.count > 0) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedSection.ordinal,
                        modifier = Modifier.testTag("tickers_tab_row"),
                    ) {
                        TickersSection.entries.forEach { section ->
                            Tab(
                                selected = selectedSection == section,
                                onClick = { tickersController.selectSection(section) },
                                text = {
                                    Text(
                                        stringResource(
                                            if (section == TickersSection.MARKET) {
                                                R.string.market_tab
                                            } else {
                                                R.string.favorites_tab
                                            },
                                        ),
                                    )
                                },
                                modifier = Modifier.testTag(
                                    if (section == TickersSection.MARKET) {
                                        "tickers_market_tab"
                                    } else {
                                        "tickers_favorites_tab"
                                    },
                                ),
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            val shouldShow by remember {
                derivedStateOf {
                    floatingState.floatingButtonState is FloatingButtonState.Show
                }
            }
            AnimatedVisibility(
                visible = shouldShow,
            ) {
                MoveToFirstTileFloatingButton {
                    coroutineScope.launch {
                        lazyColumnRememberState.animateScrollToItem(index = 0)
                    }
                }
            }
        },
    ) { innerPadding: PaddingValues ->
        if (selectedSection == TickersSection.FAVORITES && favoritesState.count > 0) {
            FavoritesContent(
                innerPadding = innerPadding,
                lazyColumnRememberState = lazyColumnRememberState,
                favoritesState = favoritesState,
                isDarkMode = isDarkMode,
                textColors = textColors,
                onSelectFavorite = onSelectFavorite,
                valueFormatter = valueFormatter,
                quoteCurrency = quoteCurrency,
            )
        } else {
            Content(
                innerPadding,
                lazyColumnRememberState,
                contentData,
                isDarkMode,
                textColors,
                marketOverviewHeaderContent,
                onOpenRecentTickers,
                onSelectTicker,
                valueFormatter,
                quoteCurrency,
                marketOverviewQuoteData,
            )
        }
    }
}

@Composable
private fun FavoritesContent(
    innerPadding: PaddingValues,
    lazyColumnRememberState: LazyListState,
    favoritesState: TickerFavoritesState,
    isDarkMode: Boolean,
    textColors: AppTextColors,
    onSelectFavorite: (id: String, name: String) -> Unit,
    valueFormatter: CryptoValueFormatter,
    quoteCurrency: QuoteCurrency,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(innerPadding)
            .testTag("ticker_favorites_list"),
        state = lazyColumnRememberState,
        contentPadding = PaddingValues(vertical = 24.dp),
    ) {
        items(
            items = favoritesState.items,
            key = { item -> item.ref.itemId },
        ) { item ->
            val ticker = item.ticker
            if (ticker != null) {
                TickerTile(
                    isDarkMode = isDarkMode,
                    textColor = textColors.primary,
                    secondaryTextColor = textColors.secondary,
                    ticker = ticker,
                    valueFormatter = valueFormatter,
                    onClick = { _, _ -> onSelectFavorite(ticker.id, ticker.name) },
                    quoteCurrency = quoteCurrency,
                )
            } else {
                UnavailableFavoriteTile(
                    item = item,
                    isDarkMode = isDarkMode,
                    textColors = textColors,
                    onClick = {
                        onSelectFavorite(item.ref.itemId, item.ref.itemId)
                    },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun UnavailableFavoriteTile(
    item: FavoriteTickerState,
    isDarkMode: Boolean,
    textColors: AppTextColors,
    onClick: () -> Unit,
) {
    val cardColor = if (isDarkMode) {
        dev.bristot.cryptoapp.ui.theme.CryptoTheme.CardDark.copy(alpha = .55f)
    } else {
        Color.White
    }
    val borderColor = if (isDarkMode) Color(0xFF293548) else Color(0xFFE2E8F0)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("ticker_favorite_unavailable_${item.ref.itemId}"),
        shape = RoundedCornerShape(18.dp),
        color = cardColor,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = item.ref.itemId,
                color = textColors.primary,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = item.error ?: stringResource(R.string.favorite_ticker_loading),
                color = if (item.error != null) MaterialTheme.colorScheme.error else textColors.secondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag(
                    if (item.isLoading) {
                        "ticker_favorite_loading_${item.ref.itemId}"
                    } else {
                        "ticker_favorite_error_${item.ref.itemId}"
                    },
                ),
            )
        }
    }
}


@TraceRecomposition
@Composable
private fun Content(
    innerPadding: PaddingValues,
    lazyColumnRememberState: LazyListState,
    contentData: MarketContainerData.HasContent,
    isDarkMode: Boolean,
    textColors: AppTextColors,
    marketOverviewHeaderContent: @Composable (
        isDarkMode: Boolean,
        textColors: AppTextColors,
        quoteData: MarketOverviewQuoteData?,
    ) -> Unit,
    onOpenRecentTickers: () -> Unit,
    onSelectTicker: (Ticker) -> Unit,
    valueFormatter: CryptoValueFormatter,
    quoteCurrency: QuoteCurrency,
    marketOverviewQuoteData: MarketOverviewQuoteData?,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 24.dp, end = 24.dp)
            .padding(innerPadding),
        state = lazyColumnRememberState,
    ) {
        items(
            count = contentData.size,
            key = contentData::extractByIndex,
            contentType = contentData::extractType
        ) { index ->
            when (contentData.extractType(index)) {
                MarketContainerData.HasContent.ContentType.HEADER_VIEW -> {
                    marketOverviewHeaderContent(isDarkMode, textColors, marketOverviewQuoteData)
                    if (contentData.recentTickersData.isNotEmpty() || contentData.tickersData.isNotEmpty()) Spacer(
                        modifier = Modifier.height(
                            24.dp
                        )
                    )
                }

                MarketContainerData.HasContent.ContentType.RECENT_TICKERS -> {
                    RecentTickersSection(
                        tickers = contentData.recentTickersData,
                        isDarkMode = isDarkMode,
                        textColors = textColors,
                        onTitleClick = onOpenRecentTickers,
                        onTickerClick = onSelectTicker,
                        valueFormatter = valueFormatter,
                        quoteCurrency = quoteCurrency,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                MarketContainerData.HasContent.ContentType.TICKERS_LOADING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                MarketContainerData.HasContent.ContentType.TICKERS_ERROR -> {
                    Text(
                        modifier = Modifier.padding(24.dp),
                        text = contentData.tickerError ?: "Unexpected error occurred",
                        color = Color.Red,
                    )
                }

                MarketContainerData.HasContent.ContentType.TICKER_TILE -> {
                    val ticker = contentData.extractTickerByIndex(index)
                    TickerTile(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        ticker = ticker,
                        valueFormatter = valueFormatter,
                        onClick = { _, _ -> onSelectTicker(ticker) },
                        quoteCurrency = quoteCurrency,
                    )
                    if (index < contentData.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                MarketContainerData.HasContent.ContentType.UNKNOWN -> throw IllegalStateException(
                    "No Content at $index"
                )
            }
        }
    }
}

@Immutable
private sealed class MarketContainerData(
    open val tickersData: List<Ticker>,
    open val recentTickersData: List<Ticker>,
    open val isTickersLoading: Boolean,
) {

    data class HasContent(
        override val tickersData: List<Ticker>,
        override val recentTickersData: List<Ticker>,
        override val isTickersLoading: Boolean,
        val tickerError: String?,
    ) : MarketContainerData(
        tickersData = tickersData,
        recentTickersData = recentTickersData,
        isTickersLoading = isTickersLoading,
    ) {

        enum class ContentType {
            HEADER_VIEW, RECENT_TICKERS, TICKERS_LOADING, TICKERS_ERROR, TICKER_TILE, UNKNOWN
        }

        private val hasRecentTickers = recentTickersData.isNotEmpty()
        private val tickerRowsStartIndex = 1 + if (hasRecentTickers) 1 else 0
        private val tickerRowsCount = when {
            isTickersLoading -> 1
            tickerError != null && tickersData.isEmpty() -> 1
            else -> tickersData.size
        }

        val size: Int = tickerRowsStartIndex + tickerRowsCount

        fun extractType(index: Int): ContentType = when {
            index == 0 -> ContentType.HEADER_VIEW
            hasRecentTickers && index == 1 -> ContentType.RECENT_TICKERS
            isTickersLoading && index == tickerRowsStartIndex -> ContentType.TICKERS_LOADING
            tickerError != null && tickersData.isEmpty() && index == tickerRowsStartIndex -> ContentType.TICKERS_ERROR
            !isTickersLoading && index in tickerRowsStartIndex until size -> ContentType.TICKER_TILE
            else -> ContentType.UNKNOWN
        }

        fun extractByIndex(index: Int): String = when (extractType(index)) {
            ContentType.HEADER_VIEW -> "header-review"
            ContentType.RECENT_TICKERS -> "recent-tickers"
            ContentType.TICKERS_LOADING -> "above-loading"
            ContentType.TICKERS_ERROR -> "tickers-error"
            ContentType.TICKER_TILE -> "ticker-tile-${extractTickerByIndex(index).id}"
            ContentType.UNKNOWN -> "no-content"
        }

        fun extractTickerByIndex(index: Int): Ticker =
            tickersData[index - tickerRowsStartIndex]
    }
}
