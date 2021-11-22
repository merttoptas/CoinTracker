package com.merttoptas.cointracker.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.merttoptas.cointracker.data.local.database.CoinListEntity
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class CoinResponse(
    @SerializedName("id")
    val coinId: String?,
    val symbol: String?,
    val name: String?,
    val image: String?,
    @SerializedName("current_price")
    val currentPrice: Double?,
    @SerializedName("price_change_percentage_24h")
    val changePercent: Double?
) : Parcelable, Serializable

fun CoinResponse.toCoinListEntity() = CoinListEntity(
    coinId = this.coinId ?: "",
    symbol = this.symbol ?: "",
    name = this.name ?: "",
    image = this.image ?: "",
    currentPrice = this.currentPrice ?: 0.0,
    changePercent = this.changePercent ?: 0.0
)