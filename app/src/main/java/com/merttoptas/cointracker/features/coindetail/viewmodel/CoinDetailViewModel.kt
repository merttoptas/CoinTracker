package com.merttoptas.cointracker.features.coindetail.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.data.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val firebaseService: FirebaseService,
    savedStateHandle: SavedStateHandle
) :
    BaseViewModel<CoinDetailViewState, CoinDetailViewEffect>() {

    private val coroutineScope = MainScope()

    init {
        coroutineScope.launch {
            setState { currentState.copy(isLoading = true) }
            savedStateHandle.get<String>(CoinDetailFragment.COIN_ID)?.let { safeCoinId ->
                coinRepository.getCoinDetail(safeCoinId).collect {
                    when (it) {
                        is DataState.Success -> {
                            setState {
                                currentState.copy(
                                    coinDetail = it.data,
                                    isLoading = false,
                                    coinId = safeCoinId
                                )
                            }
                            getFavoriteCoins()
                        }
                        is DataState.Error -> {
                            setState {
                                currentState.copy(
                                    errorMessage = it.apiError?.message,
                                    isLoading = false
                                )
                            }
                        }
                        is DataState.Loading -> {
                            setState { currentState.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }

    fun updateFavoriteCoin() {
        val data = HashMap<String, String>()

        data["id"] = currentState.coinId.toString()
        data["name"] = currentState.coinDetail?.name ?: ""
        data["symbol"] = currentState.coinDetail?.symbol ?: ""
        data["image"] = currentState.coinDetail?.image?.imageLarge ?: ""
        data["currentPrice"] = currentState.coinDetail?.marketData?.current_price?.usd.toString()
        data["changePercent"] = currentState.coinDetail?.marketData?.priceChancePercentage_24h.toString()

        setState { currentState.copy(coin = data) }

        currentState.favoriteCoins?.find { coinResponse -> coinResponse.coinId == currentState.coinId }
            ?.let {
                firebaseService.deleteFavoriteCoin(
                    firebaseService.getUid() ?: "",
                    currentState.coin
                )?.let {
                    setEffect(
                        CoinDetailViewEffect.StatusFavorite(
                            status = it.first,
                            errorMessage = it.second
                        )
                    )
                    setState { currentState.copy(isFavorite = false) }
                }
                prepareFavoriteList()

            } ?: kotlin.run {
            firebaseService.insertFavoriteCoins(
                firebaseService.getUid() ?: "",
                coin = currentState.coin
            )?.let {
                setEffect(
                    CoinDetailViewEffect.StatusFavorite(
                        status = it.first,
                        errorMessage = it.second
                    )
                )
            }
            prepareFavoriteList()
        }
    }

    private fun updateImageStatus() {
        currentState.favoriteCoins?.find { coinResponse -> coinResponse.coinId == currentState.coinId }
            ?.let {
                setState { currentState.copy(isFavorite = true) }
            } ?: kotlin.run {
            setState { currentState.copy(isFavorite = false) }
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
                    Log.d("deneme1", "forEach" + coinList.toString())
                    setState { currentState.copy(favoriteCoins = coinList) }
                }
                updateImageStatus()
            }.addOnFailureListener {
                setEffect(CoinDetailViewEffect.Failed(it.message))
            }
        }
    }

    private fun prepareFavoriteList() {
        setState { (currentState.copy(favoriteCoins = arrayListOf())) }
        getFavoriteCoins()
    }

    override fun createInitialState() = CoinDetailViewState()
}

data class CoinDetailViewState(
    val isFavorite: Boolean? = null,
    val coinId: String? = null,
    val coin: HashMap<String, String> = hashMapOf(),
    val favoriteCoins: ArrayList<CoinResponse>? = null,
    val coinDetail: CoinDetailResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

sealed class CoinDetailViewEffect : IViewEffect {
    class StatusFavorite(val status: Boolean, val errorMessage: String?) : CoinDetailViewEffect()
    class Failed(val errorMessage: String?) : CoinDetailViewEffect()
}