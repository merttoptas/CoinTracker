package com.merttoptas.cointracker.data.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.merttoptas.cointracker.data.local.database.CoinListEntity
import kotlinx.coroutines.flow.Flow

interface CoinListDataBaseRepository {

    fun insertCoinList(coinList: List<CoinListEntity>): Flow<List<Long>>
    fun searchCoin(query: SupportSQLiteQuery): Flow<List<CoinListEntity>>
}