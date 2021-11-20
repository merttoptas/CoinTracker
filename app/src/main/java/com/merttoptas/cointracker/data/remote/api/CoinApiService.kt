package com.merttoptas.cointracker.data.remote.api

import com.merttoptas.cointracker.data.model.ApiStatusResponse
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinService {

    @GET(Constants.COIN_LIST)
    suspend fun getCoinList(): Response<List<CoinResponse>>

    @GET(Constants.CHECK_API_STATUS)
    suspend fun checkApiStatus(): Response<ApiStatusResponse>

    @GET("${Constants.COIN_DETAIL}/{id}")
    suspend fun getCoinDetail(
        @Path("id") coinsId: String
    ): Response<CoinDetailResponse>
}