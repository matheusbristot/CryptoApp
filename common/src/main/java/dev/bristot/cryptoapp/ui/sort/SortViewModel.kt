package dev.bristot.cryptoapp.ui.sort

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val state: StateFlow<SortState> = savedStateHandle.getStateFlow(KEY_SORT_CONFIG, SortState())

    fun changeType(type: SortType) = update { current ->
        current.takeUnless { it.type == type }?.copy(type = type)
    }

    fun changeOrder(order: SortOrder) = update { current ->
        current.takeUnless { it.order == order }?.copy(order = order)
    }

    private inline fun update(transform: (SortState) -> SortState?) {
        transform(state.value)?.let { savedStateHandle[KEY_SORT_CONFIG] = it }
    }

    private companion object {
        const val KEY_SORT_CONFIG = "sort_config"
    }
}
