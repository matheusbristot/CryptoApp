package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailComponent
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailController
import dev.bristot.cryptoapp.feature.coins.presentation.detail.CoinDetailState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListSection
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.FavoriteCoinItem
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListLoading
import dev.bristot.cryptoapp.feature.coins.presentation.widgets.CoinListTile
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import dev.bristot.cryptoapp.format.DefaultCryptoValueFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ComposeComponentsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun coinListLoading_showsProgressIndicator() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListLoading()
            }
        }

        composeRule.onNodeWithTag("coin_list_loading").assertIsDisplayed()
    }

    @Test
    fun coinListTile_displaysCoinInfo() {
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListTile(
                    valueFormatter = DefaultCryptoValueFormatter(),
                    coin = Coin(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        rank = 1,
                        isNew = false,
                        isActive = true,
                        type = "coin",
                    )
                )
            }
        }

        composeRule.onNodeWithTag("coin_tile_btc").assertIsDisplayed()
        composeRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeRule.onNodeWithText("BTC", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("B").assertIsDisplayed()
    }

    @Test
    fun favoritesTabs_onlyExistWhileThereAreFavorites() {
        val favorites = MutableStateFlow<List<FavoriteCoinItem>>(emptyList())
        val controller = CoinListController(
            state = MutableStateFlow(CoinListState.Initial),
            favorites = favorites,
            selectedSection = MutableStateFlow(CoinListSection.ALL),
            refreshIfNeeded = {},
            setActive = {},
            handleToTop = {},
            sortBy = {},
            selectSection = {},
        )
        val sortController = SortController(MutableStateFlow(SortState()), {}, {})
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinListComponent(
                    controller = controller,
                    sortController = sortController,
                    valueFormatter = DefaultCryptoValueFormatter(),
                )
            }
        }

        composeRule.onAllNodesWithTag("coin_tab_favorites").assertCountEquals(0)
        composeRule.runOnIdle {
            favorites.value = listOf(FavoriteCoinItem("btc", null))
        }
        composeRule.onNodeWithTag("coin_tab_all").assertIsDisplayed()
        composeRule.onNodeWithTag("coin_tab_favorites").assertIsDisplayed()
        composeRule.onNodeWithText("All").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeRule.runOnIdle { favorites.value = emptyList() }
        composeRule.onAllNodesWithTag("coin_tab_favorites").assertCountEquals(0)
    }

    @Test
    fun favoriteAction_remainsAvailableOnDetailError() {
        var toggled = false
        val controller = CoinDetailController(
            state = MutableStateFlow(
                CoinDetailState(isLoading = false, errorMessage = "offline"),
            ),
            refreshIfNeeded = {},
            toggleFavorite = { toggled = true },
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                CoinDetailComponent(
                    name = "Bitcoin",
                    controller = controller,
                    valueFormatter = DefaultCryptoValueFormatter(),
                    showBackButton = true,
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("coin_detail_error").assertIsDisplayed()
        composeRule.onNodeWithTag("coin_favorite_button").performClick()
        composeRule.runOnIdle { assertTrue(toggled) }
    }
}
