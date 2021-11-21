package com.merttoptas.cointracker.data.local.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinList(coinList: List<CoinListEntity>): List<Long>

    @RawQuery(observedEntities = [CoinListEntity::class])
    suspend fun searchCoin(query: SupportSQLiteQuery): List<CoinListEntity>

    @Update
    suspend fun changeCoinListEntity(coinList: List<CoinListEntity>)

}