package dev.bristot.cryptoapp.presentation.coin_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.bristot.cryptoapp.R
import dev.bristot.cryptoapp.presentation.coin_list.viewmodel.CoinListState
import dev.bristot.cryptoapp.presentation.coin_list.viewmodel.CoinListViewModel
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinList
import dev.bristot.cryptoapp.presentation.coin_list.widgets.CoinListLoading
import dev.bristot.cryptoapp.ui.widgets.floating_button.MoveToFirstTileFloatingButton
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListComponent(
    modifier: Modifier = Modifier,
    listViewModel: CoinListViewModel = hiltViewModel(),
    internalScope: CoroutineScope = rememberCoroutineScope(),
    lazyColumnRememberState: LazyListState = rememberLazyListState()
) {

    val state = listViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lazyColumnRememberState) {
        snapshotFlow { lazyColumnRememberState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            if (state.value is CoinListState.SuccessWithUIProperties) {
                listViewModel.handleToTop(shouldShow = firstVisibleItemIndex > 5)
            }
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(
            modifier = modifier,
            title = {
                Text("CryptoApp")
            },
            actions = {
                AnimatedVisibility(visible = state.value is CoinListState.SuccessWithUIProperties) {
                    val stateSortFilter = (state.value as CoinListState.SuccessWithUIProperties)
                    Box {
                        Icon(
                            modifier = Modifier.clickable {
                                    listViewModel.showPopUp()
                                },
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.more_vert_24,
                            ),
                            contentDescription = null,
                        )
                        DropdownMenu(
                            properties = PopupProperties(
                                dismissOnBackPress = true, dismissOnClickOutside = true
                            ),
                            expanded = stateSortFilter.sortPopVisibility,
                            onDismissRequest = listViewModel::dismissPopUp
                        ) {
                            DropdownMenuItem(text = { Text("Name") }, onClick = {
                                listViewModel.changeSortType(SortType.NAME)
                            })
                            DropdownMenuItem(
                                text = {
                                    Text("Symbol")
                                },
                                onClick = {
                                    listViewModel.changeSortType(SortType.SYMBOL)
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Rank")
                                },
                                onClick = {
                                    listViewModel.changeSortType(SortType.RANK)
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Asc")
                                },
                                onClick = {
                                    listViewModel.changeSort(SortOrder.ASCENDING)
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Desc")
                                },
                                onClick = {
                                    listViewModel.changeSort(SortOrder.DESCENDING)
                                },
                            )
                        }
                    }
                }
            },
        )
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = (state.value is CoinListState.SuccessWithUIProperties) && (state.value as CoinListState.SuccessWithUIProperties).toTopVisibility,
        ) {
            MoveToFirstTileFloatingButton() {
                listViewModel.handleToTop(shouldShow = false)
            }
        }
    }) { innerPadding ->
        when (val state: CoinListState = state.value) {
            is CoinListState.Loading -> CoinListLoading(modifier = modifier)

            is CoinListState.Error -> {}
            is CoinListState.Success -> CoinList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                lazyColumnRememberState = lazyColumnRememberState,
                coins = state.coins,
            )

            else -> {

            }
        }
    }
}


