package com.merttoptas.cointracker.features.coinlist.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.coinlist.CoinListUseCase
import com.merttoptas.cointracker.domain.usecase.coinlist.CoinListViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.coinlist.CoinListViewState
import com.merttoptas.cointracker.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(private val coinListUseCase: CoinListUseCase) : BaseViewModel<CoinListViewState, CoinListViewEvent>() {

    init {
        sendToEvent(CoinListViewEvent.LoadInitialData(viewState = uiState.value))
    }

    fun sendToEvent(event: CoinListViewEvent) {
        viewModelScope.launch {
            coinListUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> setState { it.data }
                    is ViewData.Event -> setEvent(it.data)

                }
            }
        }
    }

    override fun createInitialState() = CoinListViewState()
}