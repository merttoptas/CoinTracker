package com.merttoptas.cointracker.domain.di

import com.merttoptas.cointracker.data.di.DefaultDispatcher
import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.repository.CoinDatabaseRepository
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.domain.usecase.coinlist.CoinListUseCase
import com.merttoptas.cointracker.domain.usecase.login.LoginUseCase
import com.merttoptas.cointracker.domain.usecase.splash.SplashUseCase
import com.merttoptas.cointracker.domain.usecase.main.MainActivityUseCase
import com.merttoptas.cointracker.domain.usecase.register.RegisterUseCase
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
        coinRepository: CoinRepository,
    ) = SplashUseCase(defaultDispatcher, dataStoreManager, coinRepository)

    @ViewModelScoped
    @Provides
    fun provideMainActivityUseCase() = MainActivityUseCase()

    @ViewModelScoped
    @Provides
    fun provideLoginUseCase(
        dataStoreManager: DataStoreManager,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        firebaseService: FirebaseService,
    ) = LoginUseCase(defaultDispatcher, dataStoreManager, firebaseService)

    @ViewModelScoped
    @Provides
    fun provideRegisterUseCase(
        dataStoreManager: DataStoreManager,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        firebaseService: FirebaseService,
    ) = RegisterUseCase(defaultDispatcher, dataStoreManager, firebaseService)

    @ViewModelScoped
    @Provides
    fun provideCoinListUseCase(
        coinRepository: CoinRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        firebaseService: FirebaseService,
        coinDatabaseRepository: CoinDatabaseRepository,
    ) = CoinListUseCase(defaultDispatcher, coinRepository, firebaseService, coinDatabaseRepository)
}