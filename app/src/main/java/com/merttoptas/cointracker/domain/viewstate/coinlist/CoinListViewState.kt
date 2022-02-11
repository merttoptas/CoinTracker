package com.merttoptas.cointracker.domain.viewstate.coinlist

import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

abstract class CoinListViewItem(val viewType: CoinListViewType)

data class CoinListViewState(
    val pageData: List<out CoinListViewItem>,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val query: String? = null,
    val filteredCoinList: List<out CoinListViewItem> = listOf(),
    val favoriteCoins: ArrayList<CoinResponse>? = null,
) : IViewState

data class CoinItemViewItem(
    val coinId: String?,
    val symbol: String?,
    val name: String?,
    val image: String?,
    val currentPrice: Double?,
    val changePercent: Double?
) : CoinListViewItem(CoinListViewType.COIN_LIST)


object CoinListItemLoadingViewItem : CoinListViewItem(CoinListViewType.COIN_LIST_LOADING)

enum class CoinListViewType {
    COIN_LIST,
    COIN_LIST_LOADING,
}

