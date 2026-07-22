package dev.bristot.cryptoapp.feature.tickers.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.gabrielbrasileiro.combot.rule.createCombotRule
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerContainer
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerController
import dev.bristot.cryptoapp.feature.tickers.presentation.ticker.TickerState
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.ui.theme.CryptoAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TickerContainerTest {

    @get:Rule(order = 0)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 1)
    val combotRule = createCombotRule(
        rule = composeRule,
        action = ::TickerContainerCombotAction,
        assert = ::TickerContainerCombotAssert,
    )

    @Test
    fun tickerContainer_showsLoadingAndBackButton() {
        var backClicked = false
        val state = MutableStateFlow<TickerState>(TickerState.Loading)
        val controller = TickerController(
            state = state,
            quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
            refreshIfNeeded = { },
        )

        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Bitcoin",
                    tickerController = controller,
                    valueFormatter = valueFormatter(),
                    onBackButtonClick = { backClicked = true },
                )
            }
        }

        with(combotRule.arrangement) {
            assert {
                loadingContentAndBackButtonAreDisplayed("Bitcoin")
            } action {
                clickBack()
            }
        }

        assertEquals(true, backClicked)
    }

    @Test
    fun tickerContainer_showsErrorAndSuccessContent() {
        val loadingState = MutableStateFlow<TickerState>(TickerState.Error("An error occurred"))
        val controller = TickerController(
            state = loadingState,
            quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
            refreshIfNeeded = { },
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Ethereum",
                    tickerController = controller,
                    valueFormatter = valueFormatter(),
                    onBackButtonClick = { },
                )
            }
        }

        with(combotRule.arrangement) {
            assert { errorIsDisplayed("Ethereum", "An error occurred") }
        }

        composeRule.runOnIdle {
            loadingState.value = TickerState.Success(ticker = ticker())
        }

        composeRule.waitForIdle()

        with(combotRule.arrangement) {
            assert { tickerDetailsAreDisplayed() }
        }
    }

    @Test
    fun tickerContainer_allowsFavoriteToggleWhileShowingError() {
        var toggles = 0
        val isFavorite = MutableStateFlow(false)
        val controller = TickerController(
            state = MutableStateFlow(TickerState.Error("Offline")),
            quoteCurrency = MutableStateFlow(QuoteCurrency.BRL),
            refreshIfNeeded = { },
            isFavorite = isFavorite,
            toggleFavorite = {
                toggles++
                isFavorite.value = !isFavorite.value
            },
        )
        composeRule.setContent {
            CryptoAppTheme(darkTheme = false, dynamicColor = false) {
                TickerContainer(
                    name = "Bitcoin",
                    tickerController = controller,
                    valueFormatter = valueFormatter(),
                    onBackButtonClick = { },
                )
            }
        }

        with(combotRule.arrangement) {
            assert {
                errorMessageIsDisplayed("Offline")
            } action {
                addToFavorites()
            } assert {
                removeFromFavoritesIsDisplayed()
            } action {
                removeFromFavorites()
            }
        }

        assertEquals(2, toggles)
        with(combotRule.arrangement) {
            assert { removeFromFavoritesDoesNotExist() }
        }
    }
}
