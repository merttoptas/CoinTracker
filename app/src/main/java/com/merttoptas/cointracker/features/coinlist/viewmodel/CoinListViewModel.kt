package com.merttoptas.cointracker.features.coinlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.coinlist.CoinListUseCase
import com.merttoptas.cointracker.domain.usecase.coinlist.CoinListViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.coinlist.CoinListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(private val coinListUseCase: CoinListUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(coinListUseCase.getInitialData())
    val uiState: StateFlow<CoinListViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<ViewEventWrapper<CoinListViewEvent>>()
    val uiEvent: SharedFlow<ViewEventWrapper<CoinListViewEvent>> = _uiEvent

    init {
        sendToEvent(CoinListViewEvent.LoadInitialData(viewState = uiState.value))
    }

    fun sendToEvent(event: CoinListViewEvent) {
        viewModelScope.launch {
            coinListUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> _uiState.emit(it.data)
                    is ViewData.Event -> _uiEvent.emit(it.data)

                }
            }
        }
    }
}