package com.merttoptas.cointracker.domain.viewstate.coindetail

import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

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

val timeIntervalList = listOf(
    TimeInterval("1", "Daily Price Change", true),
    TimeInterval("14", "14-Day price Change", false),
    TimeInterval("30", "Monthly Price Change", false),
    TimeInterval("max", "Max Price Change", false),
)
