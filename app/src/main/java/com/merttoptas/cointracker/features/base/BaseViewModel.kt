package com.merttoptas.cointracker.features.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : IViewState, Effect : IViewEffect> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    val currentState: State get() = viewState.value

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    private val _viewEffect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val viewEffect = _viewEffect.asSharedFlow()


    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _viewState.value = newState
    }

    protected fun setEffect(effect: Effect) {
        viewModelScope.launch { _viewEffect.emit(effect) }
    }
}