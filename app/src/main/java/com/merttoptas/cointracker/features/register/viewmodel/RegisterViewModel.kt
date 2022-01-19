package com.merttoptas.cointracker.features.register.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.register.RegisterUseCase
import com.merttoptas.cointracker.domain.usecase.register.RegisterViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.register.RegisterViewState
import com.merttoptas.cointracker.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : BaseViewModel<RegisterViewState, RegisterViewEvent>() {

    fun sendToEvent(event: RegisterViewEvent) {
        viewModelScope.launch {
            registerUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> setState { it.data }
                    is ViewData.Event -> setEvent(it.data)
                }
            }
        }
    }

    override fun createInitialState() = RegisterViewState()
}