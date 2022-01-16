package com.merttoptas.cointracker.domain.usecase.main

import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.main.MainActivityViewState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainActivityUseCase @Inject constructor() :
    IUseCase<MainActivityViewEvent, MainActivityViewState> {
    override fun getInitialData(event: MainActivityViewEvent?): MainActivityViewState {
        (event as? MainActivityViewEvent.LoadInitialData)?.isDirectToLogin?.let {
            return MainActivityViewState(isDirectToLogin = it)
        }
        return MainActivityViewState()
    }

    override fun invoke(event: ViewEventWrapper<MainActivityViewEvent>): Flow<ViewData<MainActivityViewState>> {
        TODO("Not yet implemented")
    }

}

sealed class MainActivityViewEvent() : IViewEvent {
    class LoadInitialData(val isDirectToLogin: Boolean?) : MainActivityViewEvent()
}