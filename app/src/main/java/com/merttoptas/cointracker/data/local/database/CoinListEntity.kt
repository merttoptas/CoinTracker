package com.merttoptas.cointracker.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.merttoptas.cointracker.data.model.CoinResponse

@Entity(tableName = "Coins")
data class CoinListEntity(
    @PrimaryKey @ColumnInfo(name = "coinId") @SerializedName("id")
    val coinId: String,
    @ColumnInfo(name = "coinSymbol")
    val symbol: String,
    @ColumnInfo(name = "coinName")
    val name: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "current_price")
    val currentPrice: Double,
    @ColumnInfo(name = "changePercent")
    val changePercent: Double
)

fun CoinListEntity.toCoinResponse() = CoinResponse(
    coinId = this.coinId,
    symbol = this.symbol,
    name = this.name,
    image = this.image,
    currentPrice = this.currentPrice,
    changePercent = this.changePercent
)