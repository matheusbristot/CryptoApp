package dev.bristot.cryptoapp.feature.coins.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListSection
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.CoinListState
import dev.bristot.cryptoapp.feature.coins.presentation.viewmodel.FavoriteCoinItem
import dev.bristot.cryptoapp.format.DefaultCryptoValueFormatter
import dev.bristot.cryptoapp.ui.sort.SortController
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class CoinListComponentTest {
    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        assert = ::CoinListComponentCombotAssert,
    )

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

        with(combotRule.arrangement) {
            assert { favoritesTabDoesNotExist() }
        }
        composeRule.runOnIdle {
            favorites.value = listOf(FavoriteCoinItem("btc", null))
        }
        with(combotRule.arrangement) {
            assert { allAndFavoritesTabsAreDisplayed() }
        }
        composeRule.runOnIdle { favorites.value = emptyList() }
        with(combotRule.arrangement) {
            assert { favoritesTabDoesNotExist() }
        }
    }
}
