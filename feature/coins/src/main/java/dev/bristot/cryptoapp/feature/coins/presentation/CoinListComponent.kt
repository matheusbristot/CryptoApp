package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.format.CryptoValueFormatter
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinList
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
) {
    val state by controller.state.collectAsStateWithLifecycle()
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
            TopAppBar(
                title = { Text("CryptoApp") },
                actions = {
                    AnimatedVisibility(state is CoinListState.SuccessWithUIProperties) {
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
        when (val current = state) {
            CoinListState.Loading -> CoinListLoading(modifier)
            is CoinListState.Success -> CoinList(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                lazyColumnRememberState = lazyColumnRememberState,
                coins = current.coins,
                valueFormatter = valueFormatter,
            )
            is CoinListState.Error, CoinListState.Initial -> Unit
        }
    }
}
