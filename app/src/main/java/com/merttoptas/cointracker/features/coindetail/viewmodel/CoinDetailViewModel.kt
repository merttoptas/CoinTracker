package com.merttoptas.cointracker.features.coindetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.merttoptas.cointracker.data.model.CoinDetailResponse
import com.merttoptas.cointracker.data.model.CoinResponse
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.repository.CoinRepository
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.domain.datastate.DataState
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
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

                getCoinHistory()
            }
        }
    }

    fun timeIntervalChange(value: TimeInterval?) {
        setState { currentState.copy(interval = value) }
        updateTimeInterval()
    }

    fun refreshIntervalChange(value: Int) {
        setState { currentState.copy(refreshInterval = value) }
    }

    fun updateFavoriteCoin() {
        setState { currentState.copy(coin = prepareRefreshUpdateFavoriteCoin()) }
        currentState.favoriteCoins?.find { coinResponse -> coinResponse.coinId == currentState.coinId }
            ?.let {
                firebaseService.deleteFavoriteCoin(
                    firebaseService.getUid() ?: "",
                    currentState.coin
                )?.let {
                    /*
                     setEvent(
                        CoinDetailViewEffect.Failed(
                            status = it.first,
                            errorMessage = it.second
                        )
                    )
                     */

                    setState { currentState.copy(isFavorite = false) }
                }
                prepareFavoriteList()

            } ?: kotlin.run {
            firebaseService.insertFavoriteCoins(
                firebaseService.getUid() ?: "",
                coin = currentState.coin
            )?.let {
                setState { currentState.copy(isFavorite = true) }
            }
            prepareFavoriteList()
        }
    }

    private fun updateImageStatus() {
        currentState.favoriteCoins?.find { coinResponse -> coinResponse.coinId == currentState.coinId }
            ?.let {
                firebaseService.updateFavorite(
                    firebaseService.getUid() ?: "",
                    prepareRefreshUpdateFavoriteCoin()
                )
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
                    setState { currentState.copy(favoriteCoins = coinList) }
                }
                updateImageStatus()
            }.addOnFailureListener {
             //   setEffect(CoinDetailViewEffect.Failed(true, it.message))
            }
        }
    }

    private fun prepareFavoriteList() {
        setState { (currentState.copy(favoriteCoins = arrayListOf())) }
        getFavoriteCoins()
    }

    private fun getCoinHistory() {
        coroutineScope.launch {
            currentState.coinId?.let {
                coinRepository.getCoinHistory(it, days = currentState.interval?.title ?: "1")
                    .collect {
                        when (it) {
                            is DataState.Success -> {
                                setState {
                                    currentState.copy(
                                        coinHistory = it.data.prices
                                    )
                                }
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
                            }
                        }
                    }
            }
        }
    }

    fun setRefreshInterval() {
        viewModelScope.launch {
            currentState.refreshInterval?.let {
                while (true) {
                    coinRepository.getCoinDetail(currentState.coinId ?: "").collect {
                        when (it) {
                            is DataState.Success -> {
                                setState {
                                    currentState.copy(
                                        coinDetail = it.data,
                                        isLoading = false,
                                    )
                                }
                                getCoinHistory()
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

                    if (currentState.isFavorite == true) {
                        firebaseService.updateFavorite(
                            firebaseService.getUid() ?: "",
                            prepareRefreshUpdateFavoriteCoin()
                        )
                    }

                    val refreshInterval = it * 60000
                    delay(refreshInterval.toLong())
                }
            }
        }
    }

    private fun prepareRefreshUpdateFavoriteCoin(): HashMap<String, Any> {
        val data = HashMap<String, Any>()

        data["id"] = currentState.coinId.toString()
        data["name"] = currentState.coinDetail?.name ?: ""
        data["symbol"] = currentState.coinDetail?.symbol ?: ""
        data["image"] = currentState.coinDetail?.image?.imageLarge ?: ""
        data["currentPrice"] = currentState.coinDetail?.marketData?.current_price?.usd.toString()
        data["changePercent"] =
            currentState.coinDetail?.marketData?.priceChancePercentage_24h.toString()
        return data
    }

    private fun updateTimeInterval() {
        getCoinHistory()
        with(currentState) {
            val tempTime = timeInterval
            tempTime.forEach {
                it.isSelected = it.title == interval?.title
            }
            setState { currentState.copy(timeInterval = tempTime) }
        }
    }

    override fun createInitialState() = CoinDetailViewState()
}

data class CoinDetailViewState(
    val isFavorite: Boolean? = null,
    val coinId: String? = null,
    val interval: TimeInterval? = null,
    val refreshInterval: Int? = null,
    val coinHistory: List<DoubleArray> = listOf(doubleArrayOf()),
    val coin: HashMap<String, Any> = hashMapOf(),
    val timeInterval: List<TimeInterval> = timeIntervalList,
    val favoriteCoins: ArrayList<CoinResponse>? = null,
    val coinDetail: CoinDetailResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState

val timeIntervalList = listOf(
    TimeInterval("1", "Daily Price Change", true),
    TimeInterval("14", "14-Day price Change", false),
    TimeInterval("30", "Monthly Price Change", false),
    TimeInterval("max", "Max Price Change", false),
)

sealed class CoinDetailViewEffect : IViewEvent {
    class Failed(val status: Boolean, val errorMessage: String?) : CoinDetailViewEffect()
}