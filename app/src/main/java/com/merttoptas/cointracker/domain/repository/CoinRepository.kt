package com.merttoptas.cointracker.domain.repository

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinHistoryResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.domain.datastate.DataState
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoinList(): Flow<DataState<List<CoinResponse>>>
    fun checkApiStatus(): Flow<DataState<ApiStatusResponse>>
    fun getCoinDetail(coinsId: String): Flow<DataState<CoinDetailResponse>>
    fun getCoinHistory(id: String, days: String, vs_currency: String = "usd"): Flow<DataState<CoinHistoryResponse>>
}