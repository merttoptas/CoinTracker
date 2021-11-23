package com.merttoptas.cointracker.data.model

import com.google.gson.annotations.SerializedName

data class CoinHistoryResponse(
    @SerializedName("prices")
    val prices: List<DoubleArray>
)

