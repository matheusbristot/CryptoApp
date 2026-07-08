package dev.bristot.cryptoapp.feature.market_review.presentation.market_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.market_review.domain.repository.MarketReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.TreeMap
import javax.inject.Inject

@HiltViewModel
class MarketReviewViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val marketReviewRepository: MarketReviewRepository,
) : ViewModel() {
    private val coinStateFlow = MutableStateFlow<MarketViewState>(value = MarketViewState.Initial)

    val state: StateFlow<MarketViewState>
        get() = coinStateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = MarketViewState.Initial,
        )

    init {
        viewModelScope.launch {
            coinStateFlow.update { MarketViewState.Loading }
            marketReviewRepository.getMarketReviewData()
                .flowOn(context = dispatcherProvider.default).collect(collector = { marketReview ->
                    val marketCapIsPositive = marketReview.marketCapChange24h > 0
                    val marketCapStats = MarketStats(
                        label = "Total Market Cap",
                        value = "$" + buildValue(marketReview.marketCapUsd),
                        change = buildChange(marketReview.marketCapChange24h),
                        isPositive = marketCapIsPositive
                    )

                    val volumeIsPositive = marketReview.volume24hChange24h > 0
                    val volumeStats = MarketStats(
                        label = "24h Volume",
                        value = "$" + buildValue(marketReview.volume24hUsd),
                        change = buildChange(marketReview.volume24hChange24h),
                        isPositive = volumeIsPositive
                    )

                    coinStateFlow.update {
                        MarketViewState.MarketReviewData(
                            data = listOf(
                                marketCapStats, volumeStats
                            )
                        )
                    }
                })
        }
    }

    private fun buildChange(value: Double): String = "${if (value > 0) "+" else ""}$value%"

    private fun buildValue(value: Long): String {
        if (value < 1_000_000) return value.toString()

        val suffixes = TreeMap<Long, String>().apply {
            put(1_000_000L, "M")
            put(1_000_000_000L, "B")
            put(1_000_000_000_000L, "T")
        }

        val entry = suffixes.floorEntry(value)
        val divideBy = entry?.key
        val suffix = entry?.value

        val formatted = DecimalFormat("#.##").format(value / divideBy!!.toDouble())
        return "$formatted$suffix"
    }
}
