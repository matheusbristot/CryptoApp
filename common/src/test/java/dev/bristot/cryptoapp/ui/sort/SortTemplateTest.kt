package dev.bristot.cryptoapp.ui.sort

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class SortTemplateTest {

    private val template = TestSortTemplate()
    private val items = listOf(
        Item(rank = 2, name = "Beta", symbol = "B"),
        Item(rank = 3, name = "Charlie", symbol = "C"),
        Item(rank = 1, name = "Alpha", symbol = "A"),
    )

    @Test
    fun sort_byRankAscending_ordersLowestRankFirst() {
        assertEquals(listOf("Alpha", "Beta", "Charlie"), sort(SortType.RANK, SortOrder.ASCENDING))
    }

    @Test
    fun sort_byRankDescending_ordersHighestRankFirst() {
        assertEquals(listOf("Charlie", "Beta", "Alpha"), sort(SortType.RANK, SortOrder.DESCENDING))
    }

    @Test
    fun sort_byNameAscending_ordersAlphabetically() {
        assertEquals(listOf("Alpha", "Beta", "Charlie"), sort(SortType.NAME, SortOrder.ASCENDING))
    }

    @Test
    fun sort_byNameDescending_ordersReverseAlphabetically() {
        assertEquals(listOf("Charlie", "Beta", "Alpha"), sort(SortType.NAME, SortOrder.DESCENDING))
    }

    @Test
    fun sort_bySymbolAscending_ordersAlphabetically() {
        assertEquals(listOf("Alpha", "Beta", "Charlie"), sort(SortType.SYMBOL, SortOrder.ASCENDING))
    }

    @Test
    fun sort_bySymbolDescending_ordersReverseAlphabetically() {
        assertEquals(listOf("Charlie", "Beta", "Alpha"), sort(SortType.SYMBOL, SortOrder.DESCENDING))
    }

    @Test
    fun sort_returnsNewListWithoutMutatingInput() {
        val original = items.toList()
        val result = template.sort(items, SortState())

        assertEquals(original, items)
        assertNotSame(items, result)
        assertEquals(emptyList<Item>(), template.sort(emptyList(), SortState()))
        assertEquals(listOf(items.first()), template.sort(listOf(items.first()), SortState()))
    }

    @Test
    fun sort_preservesRelativeOrderWhenValuesAreEqual() {
        val equalItems = listOf(
            Item(rank = 1, name = "Same", symbol = "S1"),
            Item(rank = 1, name = "Same", symbol = "S2"),
        )

        assertEquals(equalItems, template.sort(equalItems, SortState(type = SortType.NAME)))
    }

    private fun sort(type: SortType, order: SortOrder): List<String> =
        template.sort(items, SortState(type, order)).map(Item::name)

    private data class Item(val rank: Int, val name: String, val symbol: String)

    private class TestSortTemplate : SortTemplate<Item>() {
        override fun rankOf(item: Item) = item.rank
        override fun nameOf(item: Item) = item.name
        override fun symbolOf(item: Item) = item.symbol
    }
}
