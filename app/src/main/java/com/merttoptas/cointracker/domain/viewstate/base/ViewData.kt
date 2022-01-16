package com.merttoptas.cointracker.domain.viewstate.base

interface IViewState

interface IViewEvent

sealed class ViewEventWrapper<T : IViewEvent> {
    class LoadInitialData<T : IViewEvent> : ViewEventWrapper<T>()
    class PageEvent<T : IViewEvent>(val pageEvent: T) : ViewEventWrapper<T>()
}

sealed class ViewData<ViewState : IViewState> {
    class State<ViewState : IViewState>(val data: ViewState) :
        ViewData<ViewState>()
}