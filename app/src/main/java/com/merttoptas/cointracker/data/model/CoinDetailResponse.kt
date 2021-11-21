package com.merttoptas.cointracker.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinDetailResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("market_data")
    val marketData: MarketData?,

    @SerializedName("image")
    val image: CoinImage?,

    @SerializedName("description")
    val description: CoinDescription?,

    @SerializedName("hashing_algorithm")
    val hashing_algorithm: String?
) : Parcelable

@Parcelize
data class MarketData(
    @SerializedName("current_price")
    val current_price: CurrentPrice?,

    @SerializedName("price_change_percentage_24h")
    val priceChancePercentage_24h: Double?
) : Parcelable

@Parcelize
data class CurrentPrice(
    @SerializedName("usd")
    val usd: Double?
) : Parcelable

@Parcelize
data class CoinImage(
    @SerializedName("large")
    val imageLarge: String?
) : Parcelable

@Parcelize
data class CoinDescription(
    @SerializedName("en")
    val description_en: String?
) : Parcelable