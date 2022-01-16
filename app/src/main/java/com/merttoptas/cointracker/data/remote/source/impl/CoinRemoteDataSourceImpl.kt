package com.merttoptas.cointracker.data.remote.source.impl

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinHistoryResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.remote.api.CoinService
import com.merttoptas.cointracker.data.remote.source.CoinRemoteDataSource
import com.merttoptas.cointracker.domain.datastate.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinRemoteDataSourceImpl @Inject constructor(
    private val coinService: CoinService
) : BaseRemoteDataSource(), CoinRemoteDataSource {

    override suspend fun getCoinList(): Flow<DataState<List<CoinResponse>>> =
        getResult { coinService.getCoinList() }

    override suspend fun checkApiStatus(): Flow<DataState<ApiStatusResponse>> =
        getResult { coinService.checkApiStatus() }

    override suspend fun getCoinDetail(coinsId: String): Flow<DataState<CoinDetailResponse>> =
        getResult { coinService.getCoinDetail(coinsId) }

    override suspend fun getCoinHistory(
        id: String,
        days: String
    ): Flow<DataState<CoinHistoryResponse>>  = getResult { coinService.getCoinHistory(id, days) }
}