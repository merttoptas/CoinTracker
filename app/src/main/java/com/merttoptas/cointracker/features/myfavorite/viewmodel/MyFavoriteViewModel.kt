package com.merttoptas.cointracker.features.myfavorite.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.domain.usecase.myfavorite.MyFavoriteUseCase
import com.merttoptas.cointracker.domain.usecase.myfavorite.MyFavoriteViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.myfavorite.MyFavoriteViewState
import com.merttoptas.cointracker.features.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val myFavoriteUseCase: MyFavoriteUseCase,
) :
    BaseViewModel<MyFavoriteViewState, MyFavoriteViewEvent>() {

    init {
        sendToEvent(MyFavoriteViewEvent.LoadInitialData(viewState = uiState.value))

    }

    fun sendToEvent(event: MyFavoriteViewEvent) {
        viewModelScope.launch {
            myFavoriteUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                when (it) {
                    is ViewData.State -> setState { it.data }
                    is ViewData.Event -> setEvent(it.data)
                }
            }
        }
    }

    override fun createInitialState() = MyFavoriteViewState()
}