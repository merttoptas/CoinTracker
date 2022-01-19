package com.merttoptas.cointracker.domain.viewstate.myfavorite

import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class MyFavoriteViewState(
    val coinList: List<CoinResponse> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState
