package com.merttoptas.cointracker.features.coinlist.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.data.local.database.toCoinResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.toCoinListEntity
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.repository.CoinDatabaseRepository
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.domain.datastate.DataState
import com.merttoptas.cointracker.domain.viewstate.base.IViewState
import com.merttoptas.cointracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val firebaseService: FirebaseService,
    private val coinDao: CoinDatabaseRepository
) :
    BaseViewModel<CoinListViewState, IViewEffect>() {

    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            coinRepository.getCoinList().collect {
                when (it) {
                    is DataState.Success -> {
                        setState { currentState.copy(coinList = it.data, isLoading = false) }
                        insertCoinListDataBase(it.data)
                            getFavoriteCoins()
                    }
                    is DataState.Error -> {
                        setState {
                            currentState.copy(
                                errorMessage = it.apiError?.message,
                                isLoading = false
                            )
                        }

                    }
                    is DataState.Loading -> {
                        setState { currentState.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun getFilterQueryData(query: String) {
        viewModelScope.launch {
            getFilterCoinData(query)
        }
    }

    private fun insertCoinListDataBase(coinList: List<CoinResponse>) {
        viewModelScope.launch {
            coinList.map {
                it.toCoinListEntity()
            }.let {
                coinDao.insertCoinList(it)
            }
        }
    }

   private fun updateFavoriteCoin() {
        currentState.favoriteCoins?.forEach { favoriteCoins ->
            currentState.coinList.find { c-> c.coinId == favoriteCoins.coinId }?.let { safeList ->
                firebaseService.updateFavorite(
                    firebaseService.getUid() ?: "",
                    prepareRefreshUpdateFavoriteCoin(safeList)
                )
            }
        }
    }

    private fun getFavoriteCoins() {
        firebaseService.getUid()?.let {
            firebaseService.getFavoriteCoins(it).get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val coinList = ArrayList<CoinResponse>()
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
                    setState { currentState.copy(favoriteCoins = coinList) }
                    updateFavoriteCoin()
                }
            }.addOnFailureListener {
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

    private suspend fun getFilterCoinData(query: String) {
        viewModelScope.launch {
            coinDao.searchCoin(Utils.getSearchQuery(query)).collect {  coinListEntity ->
                coinListEntity.map { safeList ->
                    safeList.toCoinResponse()
                }.let {
                    setState { currentState.copy(filteredCoinList = it) }
                }
            }
        }
    }

    override fun createInitialState() = CoinListViewState()
}

data class CoinListViewState(
    val coinList: List<CoinResponse> = listOf(),
    val filteredCoinList: List<CoinResponse> = listOf(),
    val favoriteCoins: ArrayList<CoinResponse>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState
