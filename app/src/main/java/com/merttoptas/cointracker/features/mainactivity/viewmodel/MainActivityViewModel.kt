package com.merttoptas.cointracker.features.mainactivity.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.main.MainActivityUseCase
import com.merttoptas.cointracker.domain.usecase.main.MainActivityViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.main.MainActivityViewState
import com.merttoptas.cointracker.features.mainactivity.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
   private val mainActivityUseCase: MainActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        mainActivityUseCase.getInitialData(
            MainActivityViewEvent.LoadInitialData(savedStateHandle.get<Boolean>(MainActivity.DIRECT_TO_LOGIN))
        )
    )
    val uiState: StateFlow<MainActivityViewState> = _uiState

    fun sendToEvent(event: MainActivityViewEvent) {
        viewModelScope.launch {
            mainActivityUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> _uiState.emit(it.data)
                }
            }
        }
    }

}