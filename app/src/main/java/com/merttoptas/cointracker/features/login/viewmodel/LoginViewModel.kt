package com.merttoptas.cointracker.features.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.login.LoginUseCase
import com.merttoptas.cointracker.domain.usecase.login.LoginViewEvent
import com.merttoptas.cointracker.domain.usecase.register.RegisterViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.login.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(loginUseCase.getInitialData())
    val uiState: StateFlow<LoginViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<ViewEventWrapper<LoginViewEvent>>()
    val uiEvent: SharedFlow<ViewEventWrapper<LoginViewEvent>> = _uiEvent

    @OptIn(InternalCoroutinesApi::class)
    fun sendToEvent(event: LoginViewEvent) {
        viewModelScope.launch {
            loginUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> _uiState.emit(it.data)
                    is ViewData.Event -> _uiEvent.emit(it.data)

                }
            }
        }
    }
}