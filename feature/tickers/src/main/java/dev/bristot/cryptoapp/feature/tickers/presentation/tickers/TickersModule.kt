package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewHeaderRegistry
import dev.bristot.cryptoapp.feature.market_review.api.MarketOverviewRendererIds
import dev.bristot.cryptoapp.feature.tickers.navigation.RecentTickersDestination
import dev.bristot.cryptoapp.feature.tickers.navigation.TickerDetailDestination
import dev.bristot.cryptoapp.feature.tickers.navigation.TickersDestination
import dev.bristot.cryptoapp.navigation.LocalNavigationData
import dev.bristot.cryptoapp.navigation.LocalNavigationHostActive
import dev.bristot.cryptoapp.navigation.RootNavigationDestination
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersController
import dev.bristot.cryptoapp.feature.tickers.presentation.recents.RecentTickersViewModel
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonController
import dev.bristot.cryptoapp.ui.widgets.floating_button.FloatingButtonManager
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.sort.SortViewModel
import dev.bristot.cryptoapp.format.CryptoValueFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
object TickersModule {

    @IntoSet
    @Provides
    fun provideRootNavigationDestination(
        marketOverviewHeaderRegistry: MarketOverviewHeaderRegistry,
        valueFormatter: CryptoValueFormatter,
        settingsRepository: SettingsRepository,
    ): RootNavigationDestination =
        RootNavigationDestination(
            destination = TickersDestination,
            label = "Tickers",
            icon = Icons.AutoMirrored.Filled.ShowChart,
            order = 0,
            entryProviderInstaller = {
                entry<TickersDestination> {
                    val navigationData = LocalNavigationData.current
                    val isActive = LocalNavigationHostActive.current
                    val tickersViewModel = hiltViewModel<TickersViewModel>()
                    val floatingButtonManager = hiltViewModel<FloatingButtonManager>()
                    val sortViewModel = hiltViewModel<SortViewModel>()
                    val recentTickersViewModel = hiltViewModel<RecentTickersViewModel>()
                    val marketReviewHeaderRenderer =
                        marketOverviewHeaderRegistry.required(MarketOverviewRendererIds.MARKET_REVIEW)
                    val tickersController = remember(tickersViewModel) {
                        TickersController(
                            state = tickersViewModel.state,
                            quoteCurrency = tickersViewModel.quoteCurrency,
                            refreshIfNeeded = tickersViewModel::refreshIfNeeded,
                            sortBy = tickersViewModel::sortBy,
                            favoritesState = tickersViewModel.favoritesState,
                            selectedSection = tickersViewModel.selectedSection,
                            selectSection = tickersViewModel::selectSection,
                            setActive = tickersViewModel::setActive,
                        )
                    }
                    DisposableEffect(isActive, tickersController) {
                        tickersController.setActive(isActive)
                        onDispose { tickersController.setActive(false) }
                    }
                    val quoteCurrency by tickersController.quoteCurrency.collectAsStateWithLifecycle()
                    LaunchedEffect(isActive, tickersController) {
                        if (isActive) {
                            settingsRepository.settings.collect {
                                tickersController.refreshIfNeeded()
                            }
                        }
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
                        marketOverviewHeaderContent = { isDarkMode, textColors, quoteData ->
                            marketReviewHeaderRenderer.Render(
                                isDarkMode = isDarkMode,
                                textColors = textColors,
                                quoteData = quoteData,
                            )
                        },
                        sortController = sortController,
                        onOpenRecentTickers = {
                            navigationData.forward(RecentTickersDestination)
                        },
                        onSelectTicker = { ticker ->
                            recentTickersController.addRecentTicker(ticker)
                            navigationData.forward(
                                TickerDetailDestination(
                                    id = ticker.id,
                                    name = ticker.name,
                                )
                            )
                        },
                        onSelectFavorite = { id, name ->
                            navigationData.forward(
                                TickerDetailDestination(
                                    id = id,
                                    name = name,
                                ),
                            )
                        },
                        valueFormatter = valueFormatter,
                        quoteCurrency = quoteCurrency,
                    )
                }
            },
        )
}
