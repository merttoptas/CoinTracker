package com.merttoptas.cointracker.domain.usecase

import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.IViewState
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import kotlinx.coroutines.flow.Flow

interface IUseCase<SubViewEvent : IViewEvent, ViewState : IViewState> {
    fun getInitialData(event: SubViewEvent? = null): ViewState

    fun invoke(event: ViewEventWrapper<SubViewEvent>): Flow<ViewData<ViewState>>
}