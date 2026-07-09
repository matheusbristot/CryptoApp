package dev.bristot.cryptoapp.feature.tickers.presentation.sort

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.PopupProperties
import com.skydoves.compose.stability.runtime.TraceRecomposition

@TraceRecomposition
@Composable
fun DropdownMenuSort(
    expanded: Boolean,
    properties: PopupProperties = PopupProperties(
        dismissOnBackPress = true, dismissOnClickOutside = true
    ),
    onChangeSortType: (SortType) -> Unit,
    onChangeSortOrder: (SortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        properties = properties,
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dropdown_sort_menu")
    ) {
        DropdownMenuItem(modifier = Modifier.testTag("sort_name"), text = { Text("Name") }, onClick = {
            onChangeSortType(SortType.NAME)
        })
        DropdownMenuItem(modifier = Modifier.testTag("sort_symbol"), text = { Text("Symbol") }, onClick = {
            onChangeSortType(SortType.SYMBOL)
        })
        DropdownMenuItem(modifier = Modifier.testTag("sort_rank"), text = {
            Text("Rank")
        }, onClick = {
            onChangeSortType(SortType.RANK)
        })
        DropdownMenuItem(modifier = Modifier.testTag("sort_asc"), text = {
            Text("Asc")
        }, onClick = {
            onChangeSortOrder(SortOrder.ASCENDING)
        })
        DropdownMenuItem(modifier = Modifier.testTag("sort_desc"), text = {
            Text("Desc")
        }, onClick = {
            onChangeSortOrder(SortOrder.DESCENDING)
        })
    }
}
