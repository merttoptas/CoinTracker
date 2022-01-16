package com.merttoptas.cointracker.domain.di

import com.merttoptas.cointracker.data.di.DefaultDispatcher
import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.domain.usecase.splash.SplashUseCase
import com.merttoptas.cointracker.domain.usecase.main.MainActivityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @ViewModelScoped
    @Provides
    fun provideSplashUseCase(
        dataStoreManager: DataStoreManager,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        coinRepository: CoinRepository
    ) = SplashUseCase(defaultDispatcher, dataStoreManager, coinRepository)

    @ViewModelScoped
    @Provides
    fun provideMainActivityUseCase() = MainActivityUseCase()

}