package dev.bristot.cryptoapp.presentation.coin_list

enum class SortType {
    RANK, NAME, SYMBOL
}

enum class SortOrder {
    ASCENDING, DESCENDING
}

data class CoinListSort(
    val sortOrder: SortOrder = SortOrder.ASCENDING,
    val sortType: SortType,
)
