package com.merttoptas.cointracker.features.splash

import com.google.firebase.auth.FirebaseAuth
import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import com.merttoptas.cointracker.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val firebaseAuth: FirebaseAuth,
    private val coinRepository: CoinRepository
) : BaseViewModel<SplashViewState, SplashViewEffect>() {
    override fun createInitialState() = SplashViewState()
    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            dataStoreManager.userLogin.collect { checkUserLogin ->
                if (checkUserLogin) {
                    setEffect(SplashViewEffect.DirectToCoinList)
                } else {
                    setEffect(SplashViewEffect.DirectToLoginAndRegister)
                }
            }
        }

        coroutineScope.launch {
            coinRepository.checkApiStatus().collect {
                when (it) {
                    is DataState.Success -> {
                        setState { currentState.copy(geckoSays = it.data.gecko_says) }

                    }
                    is DataState.Error -> {
                    }
                    is DataState.Loading -> {
                    }
                }
            }
        }
    }
}


data class SplashViewState(
    val isLoading: Boolean = false,
    val geckoSays: String? = null,
) : IViewState

sealed class SplashViewEffect : IViewEffect {
    object DirectToCoinList : SplashViewEffect()
    object DirectToLoginAndRegister : SplashViewEffect()
}