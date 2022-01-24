package com.merttoptas.cointracker.domain.usecase.coindetail

import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.datastate.DataState
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.usecase.favoritecoinlist.FavoriteCoinListUseCase
import com.merttoptas.cointracker.domain.usecase.favoritecoinlist.FavoriteCoinListViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.coindetail.CoinDetailViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CoinDetailUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val coinRepository: CoinRepository,
    private val firebaseService: FirebaseService,
    private val favoriteUseCase: FavoriteCoinListUseCase,
) : IUseCase<CoinDetailViewEvent, CoinDetailViewState> {
    override fun getInitialData(event: CoinDetailViewEvent?): CoinDetailViewState {
        return CoinDetailViewState()
    }

    override fun invoke(event: ViewEventWrapper<CoinDetailViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.LoadInitialData) {
         emitAll(getCoinDetail(coinId = event.pageEvent.coinId, viewState = event.pageEvent.viewState))
        }
    }

    private fun getCoinDetail(viewState: CoinDetailViewState, coinId: String?) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            coinId?.let {
                coinRepository.getCoinDetail(coinId).collect {
                    when (it) {
                        is DataState.Success -> {
                            emit(
                                ViewData.State(
                                    viewState.copy(
                                        coinDetail = it.data,
                                        isLoading = false,
                                        coinId = coinId
                                    )
                                )
                            )
                            emitAll(getFavoriteCoinList(viewState))
                        }
                        is DataState.Error -> {
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(
                                        CoinDetailViewEvent.SnackBarError(
                                            it.apiError?.message
                                        )
                                    )
                                )
                            )
                            emit(ViewData.State(viewState.copy(isLoading = false)))
                        }
                        is DataState.Loading -> {
                            emit(ViewData.State(viewState.copy(isLoading = true)))
                        }
                    }
                    emitAll(getCoinHistory(viewState))
                }

            }
        }.flowOn(defaultDispatcher)

    private fun getFavoriteCoinList(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            favoriteUseCase.invoke(
                ViewEventWrapper.PageEvent(
                    FavoriteCoinListViewEvent.OnFavoriteCoinListData(
                        firebaseService.getUid() ?: ""
                    )
                )
            ).collect {
                when (it) {
                    is ViewData.State -> {
                        emit(ViewData.State(viewState.copy(favoriteCoins = it.data.coinList)))
                    }
                    is ViewData.Event -> {
                        if (it.data is ViewEventWrapper.PageEvent && it.data.pageEvent is FavoriteCoinListViewEvent.SnackBarError) {
                            emit(ViewData.State(viewState.copy(isLoading = false)))
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(
                                        CoinDetailViewEvent.SnackBarError(
                                            it.data.pageEvent.errorMessage
                                        )
                                    )
                                )
                            )
                        }

                    }
                }
            }
        }.flowOn(defaultDispatcher)

    private fun getCoinHistory(viewState: CoinDetailViewState) = flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
        viewState.coinId?.let {
            coinRepository.getCoinHistory(it, days = viewState.interval?.title ?: "1")
                .collect {
                    when (it) {
                        is DataState.Success -> {
                            emit(ViewData.State(viewState.copy(coinHistory = it.data.prices)))
                        }
                        is DataState.Error -> {
                            emit(ViewData.State(viewState.copy(isLoading = false)))
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(
                                        CoinDetailViewEvent.SnackBarError(
                                            it.apiError?.message
                                        )
                                    )
                                )
                            )
                        }
                        is DataState.Loading -> {
                        }
                    }
                }
        }
    }
}

sealed class CoinDetailViewEvent : IViewEvent {
    class LoadInitialData(val viewState: CoinDetailViewState,val coinId: String?) : CoinDetailViewEvent()

    class SnackBarError(val errorMessage: String?) : CoinDetailViewEvent()
}
