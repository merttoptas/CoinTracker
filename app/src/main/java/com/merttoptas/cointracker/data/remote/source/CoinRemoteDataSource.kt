package com.merttoptas.cointracker.data.remote.source

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinHistoryResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CoinRemoteDataSource {

    suspend fun getCoinList(): Flow<DataState<List<CoinResponse>>>
    suspend fun checkApiStatus(): Flow<DataState<ApiStatusResponse>>
    suspend fun getCoinDetail(id: String): Flow<DataState<CoinDetailResponse>>
    suspend fun getCoinHistory(
        id: String,
        days: String
    ): Flow<DataState<CoinHistoryResponse>>
}