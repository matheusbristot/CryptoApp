package dev.bristot.cryptoapp.feature.coins.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.usecase.GetQuotedCoinsUseCase
import dev.bristot.cryptoapp.ui.sort.SortState
import dev.bristot.cryptoapp.ui.sort.SortTemplate
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val getQuotedCoins: GetQuotedCoinsUseCase,
    private val sortTemplate: SortTemplate<Coin>,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val coinStateFlow = MutableStateFlow<CoinListState>(CoinListState.Initial)
    private var lastRequestedQuote: QuoteCurrency? = null
    private var refreshJob: Job? = null

    val state: StateFlow<CoinListState> = coinStateFlow.asStateFlow()

    fun handleToTop(shouldShow: Boolean) {
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties ?: return
        coinStateFlow.update { current.copy(toTopVisibility = shouldShow) }
    }

    fun sortBy(sortState: SortState) {
        val current = coinStateFlow.value as? CoinListState.SuccessWithUIProperties ?: return
        coinStateFlow.update {
            current.copy(coins = sortTemplate.sort(current.coins, sortState))
        }
    }

    fun refreshIfNeeded() {
        val selectedQuote = settingsRepository.settings.value.selectedQuoteCurrency
        val hasUsableResult = coinStateFlow.value is CoinListState.SuccessWithUIProperties
        if (selectedQuote == lastRequestedQuote &&
            (refreshJob?.isActive == true || hasUsableResult)
        ) return

        lastRequestedQuote = selectedQuote
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            coinStateFlow.update { CoinListState.Loading }
            try {
                val coins = getQuotedCoins(selectedQuote)
                coinStateFlow.update {
                    CoinListState.SuccessWithUIProperties(
                        coins = sortTemplate.sort(coins, SortState()),
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                coinStateFlow.update { CoinListState.Error("An error occurred") }
            }
        }
    }
}
