package dev.bristot.cryptoapp.feature.tickers.presentation.sort

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SortState(val type: SortType, val order: SortOrder) : Parcelable

enum class SortType {
    RANK, NAME, SYMBOL
}

enum class SortOrder {
    ASCENDING, DESCENDING
}
