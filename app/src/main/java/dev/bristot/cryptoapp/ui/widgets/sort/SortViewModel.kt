package dev.bristot.cryptoapp.ui.widgets.sort

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val state: StateFlow<SortState>
        get() = sortState

    private val sortState = savedStateHandle.getStateFlow(
        KEY_SORT_CONFIG, SortState(type = SortType.RANK, order = SortOrder.ASCENDING)
    )

    fun changeType(sortType: SortType) {
        savedStateHandle.get<SortState>(KEY_SORT_CONFIG)?.let { state ->
            if (sortType != state.type) return@let state.copy(type = sortType)
            return@let null
        }?.also { newState ->
            savedStateHandle[KEY_SORT_CONFIG] = newState
        }
    }

    fun changeOrder(sortOrder: SortOrder) {
        savedStateHandle.get<SortState>(KEY_SORT_CONFIG)?.let { state ->
            if (sortOrder != state.order) return@let state.copy(order = sortOrder)
            return@let null
        }?.also { newState ->
            savedStateHandle[KEY_SORT_CONFIG] = newState
        }
    }

    companion object {
        private const val KEY_SORT_CONFIG = "sort_config"
    }
}