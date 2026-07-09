package dev.bristot.cryptoapp.ui.widgets.floating_button

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FloatingButtonManager @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val savedState = savedStateHandle.getStateFlow(KEY_STATE, FloatingButtonSavedState())
    private val _state = MutableStateFlow(savedState.value.toScrollStateSavable())

    val state: StateFlow<ScrollStateSavable> = _state.asStateFlow()

    fun onHandleVisibility(showButton: Boolean) {
        val currentState = savedState.value
        if (currentState.isButtonVisible == showButton) return

        val newState = currentState.copy(isButtonVisible = showButton)
        savedStateHandle[KEY_STATE] = newState
        _state.value = newState.toScrollStateSavable()
    }

    fun onSaveScroll(index: Int, offset: Int) {
        val currentState = savedState.value
        if (currentState.firstVisibleItemIndex == index && currentState.firstVisibleItemScrollOffset == offset) return

        val newState = currentState.copy(
            firstVisibleItemIndex = index,
            firstVisibleItemScrollOffset = offset,
        )
        savedStateHandle[KEY_STATE] = newState
        _state.value = newState.toScrollStateSavable()
    }

    companion object {
        private const val KEY_STATE = "floating_button_state"
    }
}

private fun FloatingButtonSavedState.toScrollStateSavable(): ScrollStateSavable {
    return ScrollStateSavable(
        floatingButtonState = if (isButtonVisible) FloatingButtonState.Show else FloatingButtonState.Hidden,
        listState = ListState(firstVisibleItemIndex, firstVisibleItemScrollOffset)
    )
}
