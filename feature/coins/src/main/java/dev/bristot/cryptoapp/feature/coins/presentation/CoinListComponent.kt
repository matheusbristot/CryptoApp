package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListSection
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinList
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.FavoriteCoinList
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListLoading
import dev.bristot.cryptoapp.ui.sort.SortComponent
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListComponent(
    controller: CoinListController,
    sortController: SortController,
    modifier: Modifier = Modifier,
    valueFormatter: CryptoValueFormatter,
    lazyColumnRememberState: LazyListState = rememberLazyListState(),
    onCoinClick: (Coin) -> Unit = {},
) {
    val state by controller.state.collectAsStateWithLifecycle()
    val favorites by controller.favorites.collectAsStateWithLifecycle()
    val selectedSection by controller.selectedSection.collectAsStateWithLifecycle()
    val sortState by sortController.state.collectAsStateWithLifecycle()
    val internalScope = rememberCoroutineScope()

    LaunchedEffect(lazyColumnRememberState, controller) {
        snapshotFlow { lazyColumnRememberState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            controller.handleToTop(firstVisibleItemIndex > 5)
        }
    }

    val scrollToTop = remember(internalScope, lazyColumnRememberState) {
        {
            internalScope.launch { lazyColumnRememberState.animateScrollToItem(index = 0) }
            Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("CryptoApp") },
                    actions = {
                        AnimatedVisibility(
                            state is CoinListState.SuccessWithUIProperties || favorites.isNotEmpty(),
                        ) {
                            SortComponent(
                                state = sortState,
                                onChangeType = { type ->
                                    val updated = sortState.copy(type = type)
                                    sortController.changeType(type)
                                    controller.sortBy(updated)
                                },
                                onChangeOrder = { order ->
                                    val updated = sortState.copy(order = order)
                                    sortController.changeOrder(order)
                                    controller.sortBy(updated)
                                },
                                onScrollToFirstIndex = scrollToTop,
                            )
                        }
                    },
                )
                if (favorites.isNotEmpty()) {
                    PrimaryTabRow(
                        selectedTabIndex = if (selectedSection == CoinListSection.ALL) 0 else 1,
                    ) {
                        Tab(
                            selected = selectedSection == CoinListSection.ALL,
                            onClick = { controller.selectSection(CoinListSection.ALL) },
                            text = { Text("All") },
                            modifier = Modifier.testTag("coin_tab_all"),
                        )
                        Tab(
                            selected = selectedSection == CoinListSection.FAVORITES,
                            onClick = { controller.selectSection(CoinListSection.FAVORITES) },
                            text = { Text("Favorites") },
                            modifier = Modifier.testTag("coin_tab_favorites"),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = (state as? CoinListState.SuccessWithUIProperties)?.toTopVisibility == true,
            ) {
                MoveToFirstTileFloatingButton {
                    controller.handleToTop(false)
                    scrollToTop()
                }
            }
        },
    ) { innerPadding ->
        if (selectedSection == CoinListSection.FAVORITES && favorites.isNotEmpty()) {
            FavoriteCoinList(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                lazyListState = lazyColumnRememberState,
                favorites = favorites,
                valueFormatter = valueFormatter,
                onCoinClick = onCoinClick,
                onUnavailableClick = { item ->
                    onCoinClick(
                        Coin(
                            id = item.id,
                            name = item.id,
                            symbol = item.id.substringBefore('-').uppercase(),
                            rank = 0,
                            isNew = false,
                            isActive = true,
                            type = "coin",
                        ),
                    )
                },
            )
        } else {
            when (val current = state) {
                CoinListState.Loading -> CoinListLoading(Modifier.padding(innerPadding))
                is CoinListState.Success -> CoinList(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    lazyColumnRememberState = lazyColumnRememberState,
                    coins = current.coins,
                    valueFormatter = valueFormatter,
                    onCoinClick = onCoinClick,
                )
                is CoinListState.Error, CoinListState.Initial -> Unit
            }
        }
    }
}
