package com.merttoptas.cointracker.data.repository.impl

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinHistoryResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.remote.source.CoinRemoteDataSource
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(private val coinRemoteDataSource: CoinRemoteDataSource) :
    CoinRepository {
    override fun getCoinList(): Flow<DataState<List<CoinResponse>>> {
        return flow { emitAll(coinRemoteDataSource.getCoinList()) }
    }

    override fun checkApiStatus(): Flow<DataState<ApiStatusResponse>> {
        return flow { emitAll(coinRemoteDataSource.checkApiStatus()) }
    }

    override fun getCoinDetail(coinsId: String): Flow<DataState<CoinDetailResponse>> {
        return flow { emitAll(coinRemoteDataSource.getCoinDetail(coinsId)) }
    }

    override fun getCoinHistory(
        id: String,
        days: String,
        vs_currency: String
    ): Flow<DataState<CoinHistoryResponse>> {
        return flow { emitAll(coinRemoteDataSource.getCoinHistory(id, days)) }
    }
}