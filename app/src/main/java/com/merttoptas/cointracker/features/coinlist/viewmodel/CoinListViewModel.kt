package com.merttoptas.cointracker.features.coinlist.viewmodel

import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(private val coinRepository: CoinRepository) :
    BaseViewModel<CoinListViewState, CoinListViewEffect>() {

    private val coroutineScope = MainScope()


    override fun createInitialState() = CoinListViewState()
}

data class CoinListViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class CoinListViewEffect : IViewEffect {
    object SuccessfullyLogin : CoinListViewEffect()
    class FailedLogin(val errorMessage: String?) : CoinListViewEffect()
}