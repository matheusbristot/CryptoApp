package dev.bristot.cryptoapp.feature.coins.presentation

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
