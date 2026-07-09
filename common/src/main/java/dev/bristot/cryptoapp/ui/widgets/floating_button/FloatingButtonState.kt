package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ScrollStateSavable(
    val floatingButtonState: FloatingButtonState, val listState: ListState
)

@Immutable
@Serializable
data class ListState(
    val firstVisibleItemIndex: Int = 0, val firstVisibleItemScrollOffset: Int = 0
)

@Serializable
sealed class FloatingButtonState(
    val minIndexToShowButton: Int = 5,
    val indexToMoveAfterClick: Int = 0,
) {
    @Serializable
    object Hidden : FloatingButtonState()

    @Serializable
    object Show : FloatingButtonState()
}
