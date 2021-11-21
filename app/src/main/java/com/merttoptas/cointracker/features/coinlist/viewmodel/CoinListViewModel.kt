package com.merttoptas.cointracker.features.coinlist.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.merttoptas.cointracker.data.local.database.CoinDao
import com.merttoptas.cointracker.data.local.database.CoinListEntity
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import com.merttoptas.cointracker.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val coinDao: CoinDao
) :
    BaseViewModel<CoinListViewState, CoinListViewEffect>() {

    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            setState { currentState.copy(isLoading = true) }
            coinRepository.getCoinList().collect {
                when (it) {
                    is DataState.Success -> {
                        setState { currentState.copy(coinList = it.data, isLoading = false) }
                        insertCoinListDataBase(it.data)
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
                CoinListEntity(coinId = it.coinId, symbol = it.symbol, name = it.name)
            }.let {
                coinDao.insertCoinList(it)
            }
        }
    }

    private suspend fun getFilterCoinData(query: String) {
        viewModelScope.launch {
            val simpleSQLiteQuery =
                SimpleSQLiteQuery("SELECT * FROM Coins WHERE coinName LIKE '%$query%' OR coinSymbol LIKE '%$query%'")
            coinDao.searchCoin(simpleSQLiteQuery).let { coinListEntitiy ->
                coinListEntitiy.map {
                    CoinResponse(coinId = it.coinId, symbol = it.symbol, name = it.name)
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class CoinListViewEffect : IViewEffect {
    object SuccessfullyLogin : CoinListViewEffect()
    class FailedLogin(val errorMessage: String?) : CoinListViewEffect()
}