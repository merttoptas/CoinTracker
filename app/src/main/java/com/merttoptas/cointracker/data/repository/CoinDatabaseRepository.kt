package com.merttoptas.cointracker.data.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.merttoptas.cointracker.data.local.database.CoinDao
import com.merttoptas.cointracker.data.local.database.CoinListEntity
import javax.inject.Inject

class CoinDatabaseRepository @Inject constructor(private val coinDao: CoinDao) {
    suspend fun insertCoinList(coinList: List<CoinListEntity>) = coinDao.insertCoinList(coinList)
    fun searchCoin(query: SupportSQLiteQuery) = coinDao.searchCoin(query)
    fun deleteAll() = coinDao.deleteAll()
}