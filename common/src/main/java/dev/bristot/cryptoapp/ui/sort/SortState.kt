package dev.bristot.cryptoapp.ui.sort

import androidx.compose.runtime.Immutable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SortState(
    val type: SortType = SortType.RANK,
    val order: SortOrder = SortOrder.ASCENDING,
) : Parcelable

enum class SortType { RANK, NAME, SYMBOL }

enum class SortOrder { ASCENDING, DESCENDING }
