package com.merttoptas.cointracker.features.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.login.LoginUseCase
import com.merttoptas.cointracker.domain.usecase.login.LoginViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.login.LoginViewState
import com.merttoptas.cointracker.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<LoginViewState, LoginViewEvent>() {

    @OptIn(InternalCoroutinesApi::class)
    fun sendToEvent(event: LoginViewEvent) {
        viewModelScope.launch {
            loginUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> setState { it.data }
                    is ViewData.Event -> setEvent(it.data)

                }
            }
        }
    }

    override fun createInitialState() = LoginViewState()
}