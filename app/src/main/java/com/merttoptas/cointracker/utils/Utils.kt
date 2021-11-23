package com.merttoptas.cointracker.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.github.aachartmodel.aainfographics.aachartcreator.*

object Utils {

    fun getSearchQuery(query: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery("SELECT * FROM Coins WHERE coinName LIKE '%$query%' OR coinSymbol LIKE '%$query%'")
    }

    fun getChartModel(title: String, subtitle: String, coinList: List<DoubleArray>): AAChartModel {
        return AAChartModel().chartType(AAChartType.Spline)
            .title(title)
            .subtitle(subtitle)
            .stacking(AAChartStackingType.Normal)
            .borderRadius(8f)
            .backgroundColor("#FFFF").animationType(AAChartAnimationType.Bounce)
            .dataLabelsEnabled(false).series(
                arrayOf(
                    AASeriesElement().name(title)
                        .data(coinList.toTypedArray()),
                )
            )
    }
}