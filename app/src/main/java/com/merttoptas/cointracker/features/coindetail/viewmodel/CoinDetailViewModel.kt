package com.merttoptas.cointracker.features.coindetail.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<CoinDetailViewState, CoinDetailViewEffect>() {

    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            setState { currentState.copy(isLoading = true) }
            savedStateHandle.get<String>(CoinDetailFragment.COIN_ID)?.let { safeCoinId ->
                coinRepository.getCoinDetail(safeCoinId).collect {
                    when (it) {
                        is DataState.Success -> {
                            setState { currentState.copy(coinDetail = it.data, isLoading = false) }
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
    }

    override fun createInitialState() = CoinDetailViewState()
}

data class CoinDetailViewState(
    val coinDetail: CoinDetailResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class CoinDetailViewEffect : IViewEffect {
    object SuccessfullyLogin : CoinDetailViewEffect()
    class FailedLogin(val errorMessage: String?) : CoinDetailViewEffect()
}