package com.merttoptas.cointracker.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.splash.SplashUseCase
import com.merttoptas.cointracker.domain.usecase.splash.SplashViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.splash.SplashViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashUseCase: SplashUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(splashUseCase.getInitialData())
    val uiState: StateFlow<SplashViewState> = _uiState

    init {
        sendToEvent(SplashViewEvent.LoadInitialData(uiState.value))
    }

    fun sendToEvent(event: SplashViewEvent) {
        viewModelScope.launch {
            splashUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> _uiState.emit(it.data)
                }
            }
        }
    }
}