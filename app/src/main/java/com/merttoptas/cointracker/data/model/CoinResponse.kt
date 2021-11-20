package com.merttoptas.cointracker.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class CoinResponse(
    @SerializedName("id")
    val coinId: String,
    val symbol: String,
    val name: String
) : Parcelable, Serializable {
    override fun toString(): String {
        return "CoinResponse(id='$coinId', symbol='$symbol', name='$name')"
    }
}