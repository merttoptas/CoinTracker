package com.merttoptas.cointracker.features.coinlist.viewmodel

import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.data.local.database.toCoinResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.toCoinListEntity
import com.merttoptas.cointracker.data.repository.CoinDatabaseRepository
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import com.merttoptas.cointracker.utils.DataState
import com.merttoptas.cointracker.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val coinDao: CoinDatabaseRepository
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
                it.toCoinListEntity()
            }.let {
                coinDao.insertCoinList(it)
            }
        }
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class CoinListViewEffect : IViewEffect {
    object SuccessfullyLogin : CoinListViewEffect()
    class FailedLogin(val errorMessage: String?) : CoinListViewEffect()
}