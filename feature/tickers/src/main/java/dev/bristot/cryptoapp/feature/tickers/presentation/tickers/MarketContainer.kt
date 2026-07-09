package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RECENT_TICKERS_PREVIEW_LIMIT
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersSection
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortController
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
fun MarketContainer(
    tickersController: TickersController,
    recentTickersController: RecentTickersController,
    floatingButtonController: FloatingButtonController,
    marketOverviewHeaderContent: @Composable (isDarkMode: Boolean, textColors: AppTextColors) -> Unit,
    sortController: SortController,
    onOpenRecentTickers: () -> Unit,
    onSelectTicker: (Ticker) -> Unit,
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)
    val floatingState by floatingButtonController.state.collectAsStateWithLifecycle()
    val tickersState by tickersController.state.collectAsStateWithLifecycle()
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

    val dropMenuVisibility = rememberSaveable {
        mutableStateOf(false)
    }

    val contentData = remember(tickersState, recentTickersState) {

        var tickers = emptyList<Ticker>()
        if ((tickersState as? TickersState.Success)?.tickers != null) {
            tickers = (tickersState as TickersState.Success).tickers
        }

        val recentTickers = recentTickersState.tickers.take(RECENT_TICKERS_PREVIEW_LIMIT)
        val recentTickerIds = recentTickers.map { ticker -> ticker.id }.toSet()
        val listedTickers = tickers.filterNot { ticker -> ticker.id in recentTickerIds }

        MarketContainerData.HasContent(
            tickersData = listedTickers,
            recentTickersData = recentTickers,
            isTickersLoading = tickersState is TickersState.Initial || tickersState is TickersState.Loading,
            tickerError = (tickersState as? TickersState.Error)?.error,
        )
    }

    val currentContentData by rememberUpdatedState(contentData)

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
            TopAppBar(
                modifier = Modifier,
                title = {
                    Text("CryptoApp")
                },
                actions = {
                    AnimatedVisibility(
                        visible = currentContentData.tickersData.isNotEmpty()
                    ) {
                        RenderSortComponent(
                            sortState,
                            dropMenuVisibility,
                            onScrollToFirstIndex = {
                                coroutineScope.launch {
                                    lazyColumnRememberState.scrollToItem(index = 0)
                                }
                            },
                            onChangeType = { type ->
                                sortController.changeType(type)
                                tickersController.sortBy(type, sortState.order)
                            },
                            onChangeOrder = { order ->
                                sortController.changeOrder(order)
                                tickersController.sortBy(sortState.type, order)
                            },
                        )
                    }
                },
            )
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
        Content(
            innerPadding,
            lazyColumnRememberState,
            contentData,
            isDarkMode,
            textColors,
            marketOverviewHeaderContent,
            onOpenRecentTickers,
            onSelectTicker
        )
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
    marketOverviewHeaderContent: @Composable (isDarkMode: Boolean, textColors: AppTextColors) -> Unit,
    onOpenRecentTickers: () -> Unit,
    onSelectTicker: (Ticker) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
                    marketOverviewHeaderContent(isDarkMode, textColors)
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
                        onClick = { _, _ -> onSelectTicker(ticker) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
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
