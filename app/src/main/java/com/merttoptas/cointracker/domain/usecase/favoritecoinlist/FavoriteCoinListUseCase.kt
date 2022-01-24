package com.merttoptas.cointracker.domain.usecase.favoritecoinlist

import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.favoritecoinlist.FavoriteCoinListViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FavoriteCoinListUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val firebaseService: FirebaseService,
) : IUseCase<FavoriteCoinListViewEvent, FavoriteCoinListViewState> {
    override fun getInitialData(event: FavoriteCoinListViewEvent?): FavoriteCoinListViewState {
        return FavoriteCoinListViewState()
    }

    override fun invoke(event: ViewEventWrapper<FavoriteCoinListViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is FavoriteCoinListViewEvent.OnFavoriteCoinListData) {
            emitAll(getFavoriteCoinList(event.pageEvent.userId))
        }
    }

    private fun getFavoriteCoinList(userId: String) =
        flow<ViewData<FavoriteCoinListViewState, FavoriteCoinListViewEvent>> {
            firebaseService.getFavoriteCoinList(userId).collect {
                emit(ViewData.State(FavoriteCoinListViewState(coinList = it.first)))
                it.second?.let {
                    emit(ViewData.Event(ViewEventWrapper.PageEvent(FavoriteCoinListViewEvent.SnackBarError(
                        it))))
                }
            }
        }.flowOn(defaultDispatcher)

}

sealed class FavoriteCoinListViewEvent : IViewEvent {
    class OnFavoriteCoinListData(val userId: String) : FavoriteCoinListViewEvent()
    class SnackBarError(val errorMessage: String?) : FavoriteCoinListViewEvent()
}