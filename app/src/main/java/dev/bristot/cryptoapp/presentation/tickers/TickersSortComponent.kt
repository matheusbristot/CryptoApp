package dev.bristot.cryptoapp.presentation.tickers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Filter2
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import dev.bristot.cryptoapp.R
import dev.bristot.cryptoapp.ui.widgets.sort.DropdownMenuSort
import dev.bristot.cryptoapp.ui.widgets.sort.SortOrder
import dev.bristot.cryptoapp.ui.widgets.sort.SortState
import dev.bristot.cryptoapp.ui.widgets.sort.SortType

@TraceRecomposition
@Composable
fun RenderSortComponent(
    sortState: SortState,
    dropMenuVisibility: MutableState<Boolean>,
    onScrollToFirstIndex: () -> Unit,
    onChangeType: (type: SortType) -> Unit,
    onChangeOrder: (order: SortOrder) -> Unit,
) = Box {
    fun showOffPopUpAndScrollToTop() {
        dropMenuVisibility.value = false
        onScrollToFirstIndex()
    }

    @Composable
    fun sortContentWithText(text: String, image: ImageVector) {
        Row(
            modifier = Modifier.clickable {
                dropMenuVisibility.value = !dropMenuVisibility.value
            }, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp), imageVector = image, contentDescription = "null"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }

    when {
        sortState.order != SortOrder.ASCENDING && sortState.type != SortType.RANK -> {
            Row(
                modifier = Modifier.clickable {
                    dropMenuVisibility.value = !dropMenuVisibility.value
                }, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Filter2,
                    contentDescription = "null"
                )
            }
        }

        sortState.order != SortOrder.ASCENDING -> {
            sortContentWithText(
                text = sortState.order.name.take(3), image = Icons.Outlined.SortByAlpha,
            )
        }

        sortState.type != SortType.RANK -> {
            sortContentWithText(
                text = sortState.type.name.take(3), image = Icons.Outlined.FilterList,
            )
        }

        else -> {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        dropMenuVisibility.value = !dropMenuVisibility.value
                    },
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.more_vert_24,
                ),
                contentDescription = null,
            )
        }
    }
    DropdownMenuSort(
        expanded = dropMenuVisibility.value,
        onChangeSortType = { type ->
            onChangeType(type)
            showOffPopUpAndScrollToTop()
        },
        onChangeSortOrder = { order ->
            onChangeOrder(order)
            showOffPopUpAndScrollToTop()
        },
        onDismiss = {
            dropMenuVisibility.value = !dropMenuVisibility.value
        },
    )
}