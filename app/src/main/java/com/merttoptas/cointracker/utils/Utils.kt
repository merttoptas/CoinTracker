package com.merttoptas.cointracker.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object Utils {

    fun getSearchQuery(query: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery("SELECT * FROM Coins WHERE coinName LIKE '%$query%' OR coinSymbol LIKE '%$query%'")
    }
}