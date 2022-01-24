package com.merttoptas.cointracker.domain.viewstate.coindetail

import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.domain.viewstate.base.IViewState
import com.merttoptas.cointracker.features.coindetail.viewmodel.timeIntervalList

data class CoinDetailViewState(
    val isFavorite: Boolean? = null,
    val coinId: String? = null,
    val interval: TimeInterval? = null,
    val refreshInterval: Int? = null,
    val coinHistory: List<DoubleArray> = listOf(doubleArrayOf()),
    val coin: HashMap<String, Any> = hashMapOf(),
    val timeInterval: List<TimeInterval> = timeIntervalList,
    val favoriteCoins: List<CoinResponse>? = null,
    val coinDetail: CoinDetailResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState
