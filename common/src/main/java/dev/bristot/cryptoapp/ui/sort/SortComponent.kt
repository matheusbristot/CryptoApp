package dev.bristot.cryptoapp.ui.sort

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Filter2
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun SortComponent(
    state: SortState,
    onChangeType: (SortType) -> Unit,
    onChangeOrder: (SortOrder) -> Unit,
    onScrollToFirstIndex: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box {
        SortIndicator(state = state, onClick = { expanded = !expanded })
        SortDropdownMenu(
            expanded = expanded,
            onChangeType = { type ->
                if (type != state.type) {
                    onChangeType(type)
                    onScrollToFirstIndex()
                }
                expanded = false
            },
            onChangeOrder = { order ->
                if (order != state.order) {
                    onChangeOrder(order)
                    onScrollToFirstIndex()
                }
                expanded = false
            },
            onDismiss = { expanded = false },
        )
    }
}

@Composable
private fun SortIndicator(state: SortState, onClick: () -> Unit) {
    @Composable
    fun IndicatorWithText(text: String, image: ImageVector) = Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = image, contentDescription = "Change sort", modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text(text)
    }

    when {
        state.order != SortOrder.ASCENDING && state.type != SortType.RANK ->
            Icon(
                imageVector = Icons.Outlined.Filter2,
                contentDescription = "Change sort",
                modifier = Modifier.size(24.dp).clickable(onClick = onClick),
            )
        state.order != SortOrder.ASCENDING -> IndicatorWithText(state.order.name.take(3), Icons.Outlined.SortByAlpha)
        state.type != SortType.RANK -> IndicatorWithText(state.type.name.take(3), Icons.Outlined.FilterList)
        else -> Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Change sort",
            modifier = Modifier.size(24.dp).clickable(onClick = onClick),
        )
    }
}

@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    onChangeType: (SortType) -> Unit,
    onChangeOrder: (SortOrder) -> Unit,
    onDismiss: () -> Unit,
    properties: PopupProperties = PopupProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        properties = properties,
        modifier = Modifier.testTag("dropdown_sort_menu"),
    ) {
        SortType.entries.forEach { type ->
            DropdownMenuItem(
                modifier = Modifier.testTag("sort_${type.name.lowercase()}"),
                text = { Text(type.name.lowercase().replaceFirstChar(Char::uppercase)) },
                onClick = { onChangeType(type) },
            )
        }
        SortOrder.entries.forEach { order ->
            DropdownMenuItem(
                modifier = Modifier.testTag("sort_${if (order == SortOrder.ASCENDING) "asc" else "desc"}"),
                text = { Text(if (order == SortOrder.ASCENDING) "Asc" else "Desc") },
                onClick = { onChangeOrder(order) },
            )
        }
    }
}
