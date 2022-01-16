package com.merttoptas.cointracker.data.di

import com.merttoptas.cointracker.data.local.database.CoinDao
import com.merttoptas.cointracker.data.remote.api.CoinService
import com.merttoptas.cointracker.data.remote.source.CoinRemoteDataSource
import com.merttoptas.cointracker.data.remote.source.impl.CoinRemoteDataSourceImpl
import com.merttoptas.cointracker.domain.repository.CoinDatabaseRepository
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.data.repository.impl.CoinRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {

    @Provides
    fun provideCoinRemoteDataSource(coinService: CoinService): CoinRemoteDataSource =
        CoinRemoteDataSourceImpl(coinService)

    @Provides
    fun provideCoinRepository(coinRemoteDataSource: CoinRemoteDataSource): CoinRepository {
        return CoinRepositoryImpl(coinRemoteDataSource)
    }

    @Provides
    fun provideCoinDatabaseRepository(coinDao: CoinDao): CoinDatabaseRepository {
        return CoinDatabaseRepository(coinDao)
    }
}