package com.merttoptas.cointracker.domain.usecase.coindetail

import com.merttoptas.cointracker.data.model.CoinResponse
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
            emitAll(
                getCoinDetail(
                    coinId = event.pageEvent.coinId,
                    viewState = event.pageEvent.viewState.copy(coinId = event.pageEvent.coinId)
                )
            )
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.LoadInitialFavoriteCoinList) {
            emitAll(getFavoriteCoinList(event.pageEvent.viewState))
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.LoadInitialCoinHistory) {
            emitAll(getCoinHistory(event.pageEvent.viewState))
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.OnRefreshInterval) {
            emitAll(setRefreshInterval(event.pageEvent.viewState))
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.OnUpdateFavoriteCoin) {
            emitAll(updateFavoriteCoin(event.pageEvent.viewState))
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.OnSetTimeInterval) {
            emitAll(updateTimeInterval(event.pageEvent.viewState))
        }
    }

    private fun getCoinDetail(viewState: CoinDetailViewState, coinId: String?) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            coinId?.let {
                coinRepository.getCoinDetail(coinId).collect { data ->
                    when (data) {
                        is DataState.Success -> {
                            emit(
                                ViewData.State(
                                    viewState.copy(
                                        coinId = coinId,
                                        coinDetail = data.data
                                    )
                                )
                            )
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(
                                        CoinDetailViewEvent.OnLoadedCoinDetail
                                    )
                                )
                            )
                        }
                        is DataState.Error -> {
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(
                                        CoinDetailViewEvent.SnackBarError(
                                            data.apiError?.message
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
                        emit(
                            ViewData.State(
                                viewState.copy(
                                    favoriteCoins = it.data.coinList,
                                    isFavorite = updateImageStatus(viewState.copy(favoriteCoins = it.data.coinList), it.data.coinList)
                                )
                            )
                        )

                        emit(
                            ViewData.Event(
                                ViewEventWrapper.PageEvent(
                                    CoinDetailViewEvent.OnLoadedFavoriteCoinList
                                )
                            )
                        )
                    }
                    is ViewData.Event -> {
                        if (it.data is ViewEventWrapper.PageEvent && it.data.pageEvent is FavoriteCoinListViewEvent.SnackBarError) {
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

    private fun getCoinHistory(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            viewState.coinId?.let {
                coinRepository.getCoinHistory(it, days = viewState.interval?.title ?: "1")
                    .collect {
                        when (it) {
                            is DataState.Success -> {
                                emit(
                                    ViewData.State(
                                        viewState.copy(
                                            isLoading = false,
                                            coinHistory = it.data.prices,
                                        )
                                    )
                                )

                                emit(
                                    ViewData.Event(
                                        ViewEventWrapper.PageEvent(
                                            CoinDetailViewEvent.OnLoadedCoinHistory(viewState)
                                        )
                                    )
                                )
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
                                emit(ViewData.State(viewState.copy(isLoading = true)))
                            }
                        }
                    }
            }
        }.flowOn(defaultDispatcher)

    private fun setRefreshInterval(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            viewState.refreshInterval?.let {
                while (true) {
                    coinRepository.getCoinDetail(viewState.coinId ?: "").collect {
                        when (it) {
                            is DataState.Success -> {
                                emit(
                                    ViewData.State(
                                        viewState.copy(
                                            coinDetail = it.data,
                                            isLoading = false
                                        )
                                    )
                                )
                                getCoinHistory(viewState)
                            }
                            is DataState.Error -> {
                                emit(
                                    ViewData.State(
                                        viewState.copy(
                                            errorMessage = it.apiError?.message,
                                            isLoading = false
                                        )
                                    )
                                )

                            }
                            is DataState.Loading -> {
                                emit(
                                    ViewData.State(
                                        viewState.copy(
                                            isLoading = true
                                        )
                                    )
                                )
                            }
                        }
                    }

                    if (viewState.isFavorite == true) {
                        firebaseService.updateFavorite(
                            firebaseService.getUid() ?: "",
                            prepareRefreshUpdateFavoriteCoin(viewState)
                        )
                    }
                    val refreshInterval = it * 60000
                    delay(refreshInterval.toLong())
                }
            }
        }.flowOn(defaultDispatcher)

    private fun updateFavoriteCoin(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            emit(ViewData.State(viewState.copy(coin = prepareRefreshUpdateFavoriteCoin(viewState))))
            viewState.favoriteCoins?.find { coinResponse -> coinResponse.coinId == viewState.coinId }
                ?.let {
                    firebaseService.deleteFavoriteCoin(
                        firebaseService.getUid() ?: "",
                        viewState.coin
                    )?.let {
                        emit(
                            ViewData.Event(
                                ViewEventWrapper.PageEvent(
                                    CoinDetailViewEvent.SnackBarError(
                                        it.second
                                    )
                                )
                            )
                        )
                        emit(ViewData.State(viewState.copy(isLoading = false)))
                    }
                    prepareFavoriteList(viewState)

                } ?: kotlin.run {
                firebaseService.insertFavoriteCoins(
                    firebaseService.getUid() ?: "",
                    coin = viewState.coin
                )?.let {
                    emit(ViewData.State(viewState.copy(isFavorite = true)))
                }
                prepareFavoriteList(viewState)
            }
        }.flowOn(defaultDispatcher)

    private fun prepareRefreshUpdateFavoriteCoin(viewState: CoinDetailViewState): HashMap<String, Any> {
        val data = HashMap<String, Any>()

        data["id"] = viewState.coinId.toString()
        data["name"] = viewState.coinDetail?.name ?: ""
        data["symbol"] = viewState.coinDetail?.symbol ?: ""
        data["image"] = viewState.coinDetail?.image?.imageLarge ?: ""
        data["currentPrice"] = viewState.coinDetail?.marketData?.current_price?.usd.toString()
        data["changePercent"] =
            viewState.coinDetail?.marketData?.priceChancePercentage_24h.toString()
        return data
    }

    private fun updateImageStatus(viewState: CoinDetailViewState, favoriteCoins: List<CoinResponse>?) : Boolean {
            favoriteCoins?.find { coinResponse -> coinResponse.coinId == viewState.coinId }
                ?.let {
                    firebaseService.updateFavorite(
                        firebaseService.getUid() ?: "",
                        prepareRefreshUpdateFavoriteCoin(viewState.copy(favoriteCoins = favoriteCoins))
                    )
                    return  true
                } ?: kotlin.run {
                return  false
            }
        }

    private fun prepareFavoriteList(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            emit(ViewData.State(viewState.copy(favoriteCoins = arrayListOf())))
            getFavoriteCoins(viewState)
        }

    private fun getFavoriteCoins(viewState: CoinDetailViewState) =
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
                        emit(
                            ViewData.State(
                                viewState.copy(
                                    favoriteCoins = it.data.coinList,
                                    isLoading = false
                                )
                            )
                        )

                        updateImageStatus(viewState, it.data.coinList)
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

    private fun updateTimeInterval(viewState: CoinDetailViewState) =
        flow<ViewData<CoinDetailViewState, CoinDetailViewEvent>> {
            getCoinHistory(viewState)
            with(viewState) {
                val tempTime = timeInterval
                tempTime.forEach {
                    it.isSelected = it.title == interval?.title
                }
                emit(ViewData.State(viewState.copy(timeInterval = tempTime)))
            }
        }
}

sealed class CoinDetailViewEvent : IViewEvent {
    class LoadInitialData(val viewState: CoinDetailViewState, val coinId: String?) :
        CoinDetailViewEvent()

     object OnLoadedCoinDetail : CoinDetailViewEvent()

    class LoadInitialFavoriteCoinList(
        val viewState: CoinDetailViewState) :
        CoinDetailViewEvent()

    object OnLoadedFavoriteCoinList : CoinDetailViewEvent()

    class LoadInitialCoinHistory(
        val viewState: CoinDetailViewState) :
        CoinDetailViewEvent()

    class OnLoadedCoinHistory(val viewState: CoinDetailViewState) : CoinDetailViewEvent()

    class OnSetTimeInterval(val viewState: CoinDetailViewState) :
        CoinDetailViewEvent()

    class OnRefreshInterval(val viewState: CoinDetailViewState) :
        CoinDetailViewEvent()

    class OnUpdateFavoriteCoin(val viewState: CoinDetailViewState) : CoinDetailViewEvent()

    class SnackBarError(val errorMessage: String?) : CoinDetailViewEvent()
}
