package com.merttoptas.cointracker.domain.usecase.splash

import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.domain.datastate.DataState
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.splash.SplashViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SplashUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val dataStoreManager: DataStoreManager,
    private val coinRepository: CoinRepository
) : IUseCase<SplashViewEvent, SplashViewState> {
    override fun getInitialData(event: SplashViewEvent?): SplashViewState {
        return SplashViewState()
    }

    override fun invoke(event: ViewEventWrapper<SplashViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is SplashViewEvent.LoadInitialData) {
            emitAll(checkUserLogin(event.pageEvent.viewState))
            emitAll(checkApiStatus(event.pageEvent.viewState))
        }
    }

    private fun checkUserLogin(viewState: SplashViewState) = flow<ViewData<SplashViewState>> {
        dataStoreManager.userLogin.collect { checkUserLogin ->
            if (checkUserLogin) {
                emit(ViewData.State(viewState.copy(isCheckUserLogin = true)))
            } else {
                emit(ViewData.State(viewState.copy(isCheckUserLogin = false)))
            }
        }
    }.flowOn(defaultDispatcher)

    private fun checkApiStatus(viewState: SplashViewState) = flow<ViewData<SplashViewState>> {
        coinRepository.checkApiStatus().collect {
            when (it) {
                is DataState.Success -> {
                    emit(ViewData.State(viewState.copy(geckoSays = it.data.gecko_says)))
                }
                is DataState.Error -> {
                }
                is DataState.Loading -> {
                }
            }
        }
    }.flowOn(defaultDispatcher)

}

sealed class SplashViewEvent : IViewEvent {
    class LoadInitialData(val viewState: SplashViewState) : SplashViewEvent()
}