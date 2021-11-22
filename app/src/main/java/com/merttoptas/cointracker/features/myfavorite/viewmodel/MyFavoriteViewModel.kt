package com.merttoptas.cointracker.features.myfavorite.viewmodel

import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) :
    BaseViewModel<MyFavoriteViewState, IViewEffect>() {

    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            setState { currentState.copy(isLoading = true) }
            getFavoriteCoins()
        }
    }

    private fun getFavoriteCoins() {
        firebaseService.getUid()?.let {
            firebaseService.getFavoriteCoins(it).get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val coinList = ArrayList<CoinResponse>()
                    result.forEach {
                        val id = it.data["id"].toString()
                        val name = it.data["name"].toString()
                        val symbol = it.data["symbol"].toString()
                        val image = it.data["image"].toString()
                        val currentPrice = it.data["currentPrice"].toString()
                        val changePercent = it.data["changePercent"].toString()

                        val coin = CoinResponse(
                            id,
                            symbol,
                            name,
                            image,
                            currentPrice = currentPrice.toDoubleOrNull(),
                            changePercent = changePercent.toDoubleOrNull()
                        )
                        coinList.add(coin)
                    }
                    setState { currentState.copy(coinList = coinList, isLoading = false) }
                }
            }.addOnFailureListener {
                setEffect(MyFavoriteViewEffect.Failed(it.message))
                setState { currentState.copy(isLoading = false) }
            }
        }
    }

    override fun createInitialState() = MyFavoriteViewState()
}

data class MyFavoriteViewState(
    val coinList: List<CoinResponse> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class MyFavoriteViewEffect : IViewEffect {
    class Failed(val errorMessage: String?) : MyFavoriteViewEffect()
}