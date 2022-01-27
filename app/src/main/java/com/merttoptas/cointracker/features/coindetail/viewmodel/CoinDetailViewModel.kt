package com.merttoptas.cointracker.features.coindetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.coindetail.CoinDetailUseCase
import com.merttoptas.cointracker.domain.usecase.coindetail.CoinDetailViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.coindetail.CoinDetailViewState
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinDetailUseCase: CoinDetailUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CoinDetailViewState, CoinDetailViewEvent>() {

    val coinId = savedStateHandle.get<String>(CoinDetailFragment.COIN_ID)!!

    init {
        sendToEvent(
            CoinDetailViewEvent.LoadInitialData(
                uiState.value,
                coinId = savedStateHandle.get<String>(
                    CoinDetailFragment.COIN_ID
                )
            )
        )
    }

    fun sendToEvent(event: CoinDetailViewEvent) {
        viewModelScope.launch {
            coinDetailUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> setState { it.data }
                    is ViewData.Event -> setEvent(it.data)
                }
            }
        }
    }

    override fun createInitialState() = CoinDetailViewState()
}