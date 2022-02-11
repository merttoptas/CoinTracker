package com.merttoptas.cointracker.domain.usecase.coinlist

import com.merttoptas.cointracker.data.local.database.toCoinResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.toCoinListEntity
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.datastate.DataState
import com.merttoptas.cointracker.domain.repository.CoinDatabaseRepository
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.coinlist.*
import com.merttoptas.cointracker.utils.Utils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CoinListUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val coinRepository: CoinRepository,
    private val firebaseService: FirebaseService,
    private val coinDao: CoinDatabaseRepository,
) : IUseCase<CoinListViewEvent, CoinListViewState> {
    override fun getInitialData(event: CoinListViewEvent?): CoinListViewState {
        return CoinListViewState(pageData = listOf(CoinListItemLoadingViewItem))
    }

    override fun invoke(event: ViewEventWrapper<CoinListViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinListViewEvent.LoadInitialData) {
            emitAll(getCoinList(event.pageEvent.viewState))
        } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinListViewEvent.OnFilterQueryData) {
            emitAll(getFilterCoinData(event.pageEvent.viewState))
        }
    }

    private fun getCoinList(viewState: CoinListViewState) =
        flow<ViewData<CoinListViewState, CoinListViewEvent>> {
            coinRepository.getCoinList().collect {
                when (it) {
                    is DataState.Success -> {
                        val pageElements = arrayListOf<CoinListViewItem>()
                        it.data.map {
                            pageElements.add(
                                CoinItemViewItem(
                                    it.coinId,
                                    it.symbol,
                                    it.name,
                                    it.image,
                                    it.currentPrice,
                                    it.changePercent
                                )
                            )
                        }


                        emit(
                            ViewData.State(
                                viewState.copy(
                                    pageData = pageElements,
                                    isLoading = false
                                )
                            )
                        )
                        insertCoinListDataBase(it.data)
                        getFavoriteCoins(viewState, it.data)
                    }
                    is DataState.Error -> {
                        emit(
                            ViewData.State(
                                viewState.copy(
                                    isLoading = false
                                )
                            )
                        )
                        emit(
                            ViewData.Event(
                                ViewEventWrapper.PageEvent(
                                    CoinListViewEvent.SnackBarError(
                                        it.apiError?.message
                                    )
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
        }.flowOn(defaultDispatcher)

    private fun getFavoriteCoins(viewState: CoinListViewState, list: List<CoinResponse>) =
        flow<ViewData<CoinListViewState, CoinListViewEvent>> {
            emit(
                ViewData.State(
                    viewState.copy(
                        favoriteCoins = getFirebaseFavoriteCoin()
                    )
                )
            )
            updateFavoriteCoin(viewState, list)
        }

    private fun getFirebaseFavoriteCoin(): ArrayList<CoinResponse> {
        val coinList = ArrayList<CoinResponse>()
        firebaseService.getUid()?.let {
            firebaseService.getFavoriteCoins(it).get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    result.forEach {
                        val id = it.data["id"].toString()
                        val name = it.data["name"].toString()
                        val symbol = it.data["symbol"].toString()
                        val image = it.data["image"].toString()
                        val currentPrice = it.data["currentPrice"].toString()
                        val changePercent = it.data["changePercent"].toString()

                        val coin = CoinResponse(
                            id,
                            symbol,
                            name,
                            image,
                            currentPrice = currentPrice.toDoubleOrNull(),
                            changePercent = changePercent.toDoubleOrNull()
                        )
                        coinList.add(coin)
                    }
                }
            }.addOnFailureListener {
            }
        }
        return coinList
    }

    private fun updateFavoriteCoin(viewState: CoinListViewState, coinList: List<CoinResponse>) {
        viewState.favoriteCoins?.forEach { favoriteCoins ->
            coinList.find { c -> c.coinId == favoriteCoins.coinId }?.let { safeList ->
                firebaseService.updateFavorite(
                    firebaseService.getUid() ?: "",
                    prepareRefreshUpdateFavoriteCoin(safeList)
                )
            }
        }
    }

    private fun prepareRefreshUpdateFavoriteCoin(coinResponse: CoinResponse): HashMap<String, Any> {
        val data = HashMap<String, Any>()

        data["id"] = coinResponse.coinId.toString()
        data["name"] = coinResponse.name ?: ""
        data["symbol"] = coinResponse.symbol ?: ""
        data["image"] = coinResponse.image ?: ""
        data["currentPrice"] = coinResponse.currentPrice.toString()
        data["changePercent"] = coinResponse.changePercent.toString()

        return data
    }

    private fun insertCoinListDataBase(coinList: List<CoinResponse>) =
        flow<ViewData<CoinListViewState, CoinListViewEvent>> {
            coinList.map {
                it.toCoinListEntity()
            }.let {
                coinDao.insertCoinList(it)
            }
        }


    private fun getFilterCoinData(viewState: CoinListViewState) =
        flow<ViewData<CoinListViewState, CoinListViewEvent>> {
            coinDao.searchCoin(Utils.getSearchQuery(viewState.query ?: ""))
                .collect { coinListEntity ->
                    val pageElements = arrayListOf<CoinListViewItem>()
                    coinListEntity.map { safeList ->
                        safeList.toCoinResponse()
                    }.let {
                        it.map {
                            pageElements.add(
                                CoinItemViewItem(
                                    it.coinId,
                                    it.symbol,
                                    it.name,
                                    it.image,
                                    it.currentPrice,
                                    it.changePercent
                                )
                            )
                        }

                        emit(
                            ViewData.State(
                                viewState.copy(
                                    filteredCoinList = pageElements
                                )
                            )
                        )
                    }
                }
        }.flowOn(defaultDispatcher)
}

sealed class CoinListViewEvent : IViewEvent {
    class LoadInitialData(val viewState: CoinListViewState) : CoinListViewEvent()
    class OnFilterQueryData(val viewState: CoinListViewState) : CoinListViewEvent()
    class SnackBarError(val message: String?) : CoinListViewEvent()
}