package com.merttoptas.cointracker.domain.viewstate.coinlist

import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class CoinListViewState(
    val query: String? = null,
    val coinList: List<CoinResponse> = listOf(),
    val filteredCoinList: List<CoinResponse> = listOf(),
    val favoriteCoins: ArrayList<CoinResponse>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : IViewState
