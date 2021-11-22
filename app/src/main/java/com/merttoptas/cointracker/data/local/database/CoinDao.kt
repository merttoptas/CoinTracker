package com.merttoptas.cointracker.data.local.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinList(coinList: List<CoinListEntity>)

    @RawQuery(observedEntities = [CoinListEntity::class])
    fun searchCoin(query: SupportSQLiteQuery): Flow<List<CoinListEntity>>

    @Query("DELETE FROM Coins")
    fun deleteAll()
}