package dev.bristot.cryptoapp.presentation.tickers

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
import dev.bristot.cryptoapp.domain.entity.Ticker
import dev.bristot.cryptoapp.presentation.market_review.MarketReviewController
import dev.bristot.cryptoapp.presentation.market_review.MarketReviewComponent
import dev.bristot.cryptoapp.presentation.market_review.MarketStats
import dev.bristot.cryptoapp.presentation.market_review.MarketViewState
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonState
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import dev.bristot.cryptoapp.ui.widgets.sort.SortController
import dev.bristot.cryptoapp.ui.theme.AppTextColors
import dev.bristot.cryptoapp.ui.theme.rememberAppTextColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
fun MarketContainer(
    tickersController: TickersController,
    floatingButtonController: FloatingButtonController,
    marketReviewController: MarketReviewController,
    sortController: SortController,
    onSelectTicker: (id: String, name: String) -> Unit,
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColors = rememberAppTextColors(isDarkMode)
    val floatingState by floatingButtonController.state.collectAsStateWithLifecycle()
    val marketReviewState by marketReviewController.state.collectAsStateWithLifecycle()
    val tickersState by tickersController.state.collectAsStateWithLifecycle()
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

    val contentData = remember(
        tickersState, marketReviewState
    ) {

        var tickers = emptyList<Ticker>()
        if ((tickersState as? TickersState.Success)?.tickers != null) {
            tickers = (tickersState as TickersState.Success).tickers
        }

        var reviews = emptyList<MarketStats>()
        if ((marketReviewState as? MarketViewState.MarketReviewData)?.data != null) {
            reviews = (marketReviewState as MarketViewState.MarketReviewData).data
        }

        if (tickers.isEmpty() && reviews.isEmpty()) {
            MarketContainerData.NoContent
        } else {
            MarketContainerData.HasContent(
                tickersData = tickers, marketReviewData = reviews
            )
        }
    }

    val currentContentData by rememberUpdatedState(contentData)

    LaunchedEffect(lazyColumnRememberState) {
        snapshotFlow {
            Pair(
                lazyColumnRememberState.firstVisibleItemIndex,
                lazyColumnRememberState.firstVisibleItemScrollOffset
            )
        }.filter {
            currentContentData is MarketContainerData.HasContent
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
                        visible = currentContentData is MarketContainerData.HasContent && currentContentData.tickersData.isNotEmpty()
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
        when (contentData) {
            is MarketContainerData.NoContent -> {
                NoContent(innerPadding, marketReviewState, tickersState)
            }

            is MarketContainerData.HasContent -> {
                Content(
                    innerPadding,
                    lazyColumnRememberState,
                    contentData,
                    isDarkMode,
                    textColors,
                    onSelectTicker
                )
            }
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
    onSelectCoin: (id: String, name: String) -> Unit
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
                    MarketReviewComponent(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        stats = contentData.marketReviewData,
                    )
                    if (contentData.tickersData.isNotEmpty()) Spacer(
                        modifier = Modifier.height(
                            24.dp
                        )
                    )
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

                MarketContainerData.HasContent.ContentType.TICKER_TILE -> {
                    TickerTile(
                        isDarkMode = isDarkMode,
                        textColor = textColors.primary,
                        secondaryTextColor = textColors.secondary,
                        ticker = contentData.tickersData[index - 1],
                        onClick = onSelectCoin,
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

@TraceRecomposition
@Composable
private fun NoContent(
    innerPadding: PaddingValues, marketReviewState: MarketViewState, tickersState: TickersState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (marketReviewState is MarketViewState.Loading) {
            CircularProgressIndicator()
        } else if (tickersState is TickersState.Error || marketReviewState is MarketViewState.Error) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = (tickersState as? TickersState.Error)?.error
                    ?: (marketReviewState as? MarketViewState.Error)?.message
                    ?: "Unexpected error occurred",
                color = Color.Red,
            )
        }
    }
}

@Immutable
private sealed class MarketContainerData(
    open val tickersData: List<Ticker>,
    open val marketReviewData: List<MarketStats>,
) {

    data class HasContent(
        override val tickersData: List<Ticker>, override val marketReviewData: List<MarketStats>
    ) : MarketContainerData(tickersData = tickersData, marketReviewData = marketReviewData) {

        enum class ContentType {
            HEADER_VIEW, TICKERS_LOADING, TICKER_TILE, UNKNOWN
        }

        val size: Int get() = (if (marketReviewData.isNotEmpty()) 1 else 0) + (if (tickersData.isNotEmpty()) tickersData.size else 1)
        fun extractType(index: Int): ContentType {
            val key = extractByIndex(index)
            return if (key.contains("header")) ContentType.HEADER_VIEW
            else if (key.contains("loading")) ContentType.TICKERS_LOADING
            else if (key.contains("tile")) ContentType.TICKER_TILE
            else ContentType.UNKNOWN
        }

        fun extractByIndex(index: Int) = when {
            index == 0 && marketReviewData.isNotEmpty() || index == 0 && marketReviewData.isEmpty() -> "header-review"
            index == 1 && tickersData.isEmpty() -> "above-loading"
            index >= 1 && tickersData.isNotEmpty() -> {
                "ticker-tile-${tickersData[index - 1].id}"
            }

            else -> "no-content"
        }
    }

    object NoContent :
        MarketContainerData(tickersData = emptyList(), marketReviewData = emptyList())
}
