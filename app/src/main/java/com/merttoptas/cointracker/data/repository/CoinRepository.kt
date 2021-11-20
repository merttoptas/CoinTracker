package com.merttoptas.cointracker.data.repository

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoinList(): Flow<DataState<List<CoinResponse>>>
    fun checkApiStatus(): Flow<DataState<ApiStatusResponse>>
    fun getCoinDetail(coinsId: String): Flow<DataState<CoinDetailResponse>>
}