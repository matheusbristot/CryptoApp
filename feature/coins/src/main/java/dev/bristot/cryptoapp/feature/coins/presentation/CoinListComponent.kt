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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListViewModel
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinList
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListLoading
import dev.bristot.cryptoapp.ui.sort.SortComponent
import dev.bristot.cryptoapp.ui.sort.SortViewModel
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListComponent(
    modifier: Modifier = Modifier,
    listViewModel: CoinListViewModel = hiltViewModel(),
    sortViewModel: SortViewModel = hiltViewModel(),
    internalScope: CoroutineScope = rememberCoroutineScope(),
    lazyColumnRememberState: LazyListState = rememberLazyListState(),
) {
    val state by listViewModel.state.collectAsStateWithLifecycle()
    val sortState by sortViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lazyColumnRememberState) {
        snapshotFlow { lazyColumnRememberState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            if (state is CoinListState.SuccessWithUIProperties) {
                listViewModel.handleToTop(shouldShow = firstVisibleItemIndex > 5)
            }
        }
    }

    fun scrollToTop() {
        internalScope.launch { lazyColumnRememberState.animateScrollToItem(index = 0) }
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
                                sortViewModel.changeType(type)
                                listViewModel.sortBy(updated)
                            },
                            onChangeOrder = { order ->
                                val updated = sortState.copy(order = order)
                                sortViewModel.changeOrder(order)
                                listViewModel.sortBy(updated)
                            },
                            onScrollToFirstIndex = ::scrollToTop,
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
                    listViewModel.handleToTop(shouldShow = false)
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
            )
            is CoinListState.Error, CoinListState.Initial -> Unit
        }
    }
}
