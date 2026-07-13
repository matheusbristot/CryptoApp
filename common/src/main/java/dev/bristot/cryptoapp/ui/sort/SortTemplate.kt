package dev.bristot.cryptoapp.ui.sort

abstract class SortTemplate<T> {

    fun sort(items: List<T>, state: SortState): List<T> = when (state.type) {
        SortType.RANK -> items.sortBy(state.order, ::rankOf)
        SortType.NAME -> items.sortBy(state.order, ::nameOf)
        SortType.SYMBOL -> items.sortBy(state.order, ::symbolOf)
    }

    protected abstract fun rankOf(item: T): Int
    protected abstract fun nameOf(item: T): String
    protected abstract fun symbolOf(item: T): String

    private fun <R : Comparable<R>> List<T>.sortBy(
        order: SortOrder,
        selector: (T) -> R,
    ): List<T> = when (order) {
        SortOrder.ASCENDING -> sortedBy(selector)
        SortOrder.DESCENDING -> sortedByDescending(selector)
    }
}
