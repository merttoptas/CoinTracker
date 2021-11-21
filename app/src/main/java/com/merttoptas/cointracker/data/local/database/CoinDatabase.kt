package com.merttoptas.cointracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CoinListEntity::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        @Volatile
        private var instance: CoinDatabase? = null

        fun getDatabase(context: Context): CoinDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, CoinDatabase::class.java, "CoinDatabase")
                .fallbackToDestructiveMigration()
                .build()
    }
}