package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
data class FloatingButtonController(
    val state: StateFlow<ScrollStateSavable>,
    val onHandleVisibility: (Boolean) -> Unit,
    val onSaveScroll: (index: Int, offset: Int) -> Unit,
)
