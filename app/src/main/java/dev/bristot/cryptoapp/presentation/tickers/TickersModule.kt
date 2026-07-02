package dev.bristot.cryptoapp.presentation.tickers

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.presentation.market_review.MarketReviewController
import dev.bristot.cryptoapp.presentation.market_review.MarketReviewViewModel
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonManager
import dev.bristot.cryptoapp.ui.widgets.sort.SortController
import dev.bristot.cryptoapp.ui.widgets.sort.SortViewModel

@Module
@InstallIn(ActivityRetainedComponent::class)
object TickersModule {

    @IntoSet
    @Provides
    fun provideTickersNavigationData(navigationData: NavigationData): EntryProviderInstaller = {
        entry<CryptoAppDestination.Tickers> {
            val tickersViewModel = hiltViewModel<TickersViewModel>()
            val floatingButtonManager = hiltViewModel<FloatingButtonManager>()
            val marketReviewViewModel = hiltViewModel<MarketReviewViewModel>()
            val sortViewModel = hiltViewModel<SortViewModel>()
            val tickersController = remember(tickersViewModel) {
                TickersController(
                    state = tickersViewModel.state,
                    sortBy = tickersViewModel::sortBy,
                )
            }
            val sortController = remember(sortViewModel) {
                SortController(
                    state = sortViewModel.state,
                    changeType = sortViewModel::changeType,
                    changeOrder = sortViewModel::changeOrder,
                )
            }
            val marketReviewController = remember(marketReviewViewModel) {
                MarketReviewController(
                    state = marketReviewViewModel.state,
                )
            }
            MarketContainer(
                tickersController = tickersController,
                floatingButtonController = FloatingButtonController(
                    state = floatingButtonManager.state,
                    onHandleVisibility = floatingButtonManager::onHandleVisibility,
                    onSaveScroll = floatingButtonManager::onSaveScroll,
                ),
                marketReviewController = marketReviewController,
                sortController = sortController,
                onSelectTicker = onSelect(navigationData)
            )
        }
    }

    private fun onSelect(navigationData: NavigationData): (id: String, name: String) -> Unit {
        fun cryptoAppDestinationTickerDetail(
            id: String, name: String
        ): CryptoAppDestination = CryptoAppDestination.TickerDetail(id, name)

        return { id, name ->
            navigationData.forward(
                newDestination = cryptoAppDestinationTickerDetail(
                    id = id, name = name
                )
            )
        }
    }
}
