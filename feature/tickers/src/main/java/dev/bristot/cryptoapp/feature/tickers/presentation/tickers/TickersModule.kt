package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewHeaderRegistry
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewRendererIds
import dev.bristot.cryptoapp.navigation.CryptoAppDestination
import dev.bristot.cryptoapp.navigation.EntryProviderInstaller
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersViewModel
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonManager
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortController
import dev.bristot.cryptoapp.feature.tickers.presentation.sort.SortViewModel
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
object TickersModule {

    @IntoSet
    @Provides
    fun provideTickersNavigationData(
        navigationData: NavigationData,
        marketOverviewHeaderRegistry: MarketOverviewHeaderRegistry,
        valueFormatter: CryptoValueFormatter,
    ): EntryProviderInstaller = {
        entry<CryptoAppDestination.Tickers> {
            val tickersViewModel = hiltViewModel<TickersViewModel>()
            val floatingButtonManager = hiltViewModel<FloatingButtonManager>()
            val sortViewModel = hiltViewModel<SortViewModel>()
            val recentTickersViewModel = hiltViewModel<RecentTickersViewModel>()
            val marketReviewHeaderRenderer =
                marketOverviewHeaderRegistry.required(MarketOverviewRendererIds.MARKET_REVIEW)
            val tickersController = remember(tickersViewModel) {
                TickersController(
                    state = tickersViewModel.state,
                    sortBy = tickersViewModel::sortBy,
                )
            }
            val recentTickersController = remember(recentTickersViewModel) {
                RecentTickersController(
                    state = recentTickersViewModel.state,
                    addRecentTicker = recentTickersViewModel::addRecentTicker,
                )
            }
            val sortController = remember(sortViewModel) {
                SortController(
                    state = sortViewModel.state,
                    changeType = sortViewModel::changeType,
                    changeOrder = sortViewModel::changeOrder,
                )
            }
            MarketContainer(
                tickersController = tickersController,
                recentTickersController = recentTickersController,
                floatingButtonController = FloatingButtonController(
                    state = floatingButtonManager.state,
                    onHandleVisibility = floatingButtonManager::onHandleVisibility,
                    onSaveScroll = floatingButtonManager::onSaveScroll,
                ),
                marketOverviewHeaderContent = { isDarkMode, textColors ->
                    marketReviewHeaderRenderer.Render(
                        isDarkMode = isDarkMode,
                        textColors = textColors,
                    )
                },
                sortController = sortController,
                onOpenRecentTickers = {
                    navigationData.forward(CryptoAppDestination.RecentTickers)
                },
                onSelectTicker = { ticker ->
                    recentTickersController.addRecentTicker(ticker)
                    navigationData.forward(
                        CryptoAppDestination.TickerDetail(
                            id = ticker.id,
                            name = ticker.name,
                        )
                    )
                },
                valueFormatter = valueFormatter,
            )
        }
    }
}
