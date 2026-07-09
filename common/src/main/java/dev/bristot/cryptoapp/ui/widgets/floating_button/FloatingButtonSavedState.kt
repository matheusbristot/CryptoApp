package dev.bristot.cryptoapp.ui.widgets.floating_button

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
// Keep this state minimal: only what SavedStateHandle needs to restore the button.
data class FloatingButtonSavedState(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
    val isButtonVisible: Boolean = false,
) : Parcelable
