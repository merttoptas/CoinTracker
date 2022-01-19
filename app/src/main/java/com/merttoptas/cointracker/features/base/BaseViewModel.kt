package com.merttoptas.cointracker.features.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.IViewState
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : IViewState, Event : IViewEvent> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    val currentState: State get() = uiState.value

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState

    private val _uiEvent = MutableSharedFlow<ViewEventWrapper<Event>>()
    val uiEvent: SharedFlow<ViewEventWrapper<Event>> = _uiEvent


    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }

    protected fun setEvent(event: ViewEventWrapper<Event>) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}