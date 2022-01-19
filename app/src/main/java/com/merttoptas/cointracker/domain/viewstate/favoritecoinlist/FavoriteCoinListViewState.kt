package com.merttoptas.cointracker.domain.viewstate.favoritecoinlist

import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class FavoriteCoinListViewState(
    val coinList: List<CoinResponse> = listOf(),
    val errorMessage: String? = null
) : IViewState