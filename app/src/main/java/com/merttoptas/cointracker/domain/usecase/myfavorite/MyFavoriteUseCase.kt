package com.merttoptas.cointracker.domain.usecase.myfavorite

import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.usecase.favoritecoinlist.FavoriteCoinListUseCase
import com.merttoptas.cointracker.domain.usecase.favoritecoinlist.FavoriteCoinListViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.myfavorite.MyFavoriteViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MyFavoriteUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val firebaseService: FirebaseService,
    private val favoriteUseCase: FavoriteCoinListUseCase,
) : IUseCase<MyFavoriteViewEvent, MyFavoriteViewState> {
    override fun getInitialData(event: MyFavoriteViewEvent?): MyFavoriteViewState {
        return MyFavoriteViewState()
    }

    override fun invoke(event: ViewEventWrapper<MyFavoriteViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is MyFavoriteViewEvent.LoadInitialData) {
            emitAll(getFavoriteCoinList(event.pageEvent.viewState))
        }
    }

    private fun getFavoriteCoinList(viewState: MyFavoriteViewState) =
        flow<ViewData<MyFavoriteViewState, MyFavoriteViewEvent>> {
            emit(ViewData.State(viewState.copy(isLoading = true)))
            favoriteUseCase.invoke(ViewEventWrapper.PageEvent(FavoriteCoinListViewEvent.OnFavoriteCoinListData(
                firebaseService.getUid() ?: ""))).collect {
                when (it) {
                    is ViewData.State -> {
                        emit(ViewData.State(viewState.copy(coinList = it.data.coinList,
                            isLoading = false)))
                    }
                    is ViewData.Event -> {
                        if (it.data is ViewEventWrapper.PageEvent && it.data.pageEvent is FavoriteCoinListViewEvent.SnackBarError) {
                            emit(ViewData.State(viewState.copy(isLoading = false)))
                            emit(ViewData.Event(ViewEventWrapper.PageEvent(MyFavoriteViewEvent.SnackBarError(
                                it.data.pageEvent.errorMessage))))
                        }

                    }
                }
            }
        }.flowOn(defaultDispatcher)

}

sealed class MyFavoriteViewEvent : IViewEvent {
    class LoadInitialData(val viewState: MyFavoriteViewState) : MyFavoriteViewEvent()
    class SnackBarError(val errorMessage: String?) : MyFavoriteViewEvent()
}