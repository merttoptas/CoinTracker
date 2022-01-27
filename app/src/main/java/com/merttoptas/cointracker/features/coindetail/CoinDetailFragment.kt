package com.merttoptas.cointracker.features.coindetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.databinding.FragmentCoinDetailBinding
import com.merttoptas.cointracker.domain.usecase.coindetail.CoinDetailViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.adapter.OnClickListener
import com.merttoptas.cointracker.features.coindetail.adapter.TimeIntervalAdapter
import com.merttoptas.cointracker.features.coindetail.viewmodel.CoinDetailViewModel
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.Utils
import com.merttoptas.cointracker.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinDetailFragment : BaseFragment<FragmentCoinDetailBinding>(), OnClickListener {

    companion object {
        const val COIN_ID = "COIN_ID"
    }

    override val layoutId: Int = R.layout.fragment_coin_detail
    private val viewModel by viewModels<CoinDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                    binding.dataHolder = it.coinDetail
                    binding.isFavorite = it.isFavorite ?: false

                    binding.rvTimeInterval.adapter =
                        TimeIntervalAdapter(this@CoinDetailFragment).apply {
                            submitList(it.timeInterval)
                        }

                    setCharts(it.coinHistory)
                }
            }
            launch {
                viewModel.uiEvent.collect { event ->
                    if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.SnackBarError) {
                        event.pageEvent.errorMessage?.let {
                            showSnackBar(this@CoinDetailFragment, it, SnackBarEnum.ERROR)
                        }
                    } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.OnLoadedCoinDetail) {
                        viewModel.sendToEvent(CoinDetailViewEvent.LoadInitialFavoriteCoinList(viewModel.uiState.value))
                    }  else if (event is ViewEventWrapper.PageEvent && event.pageEvent is CoinDetailViewEvent.OnLoadedFavoriteCoinList) {
                        viewModel.sendToEvent(CoinDetailViewEvent.LoadInitialCoinHistory(viewModel.uiState.value))
                    }
                }
            }
        }

        binding.favouriteImageView.setOnClickListener {
            viewModel.sendToEvent(CoinDetailViewEvent.OnUpdateFavoriteCoin(viewModel.uiState.value))
        }

        binding.btnRefreshInterval.setOnClickListener {
            viewModel.sendToEvent(
                CoinDetailViewEvent.OnRefreshInterval(
                    viewModel.uiState.value.copy(
                        refreshInterval = binding.etRefreshInterval.text.toString().toInt()
                    )
                )
            )
        }
        binding.backBtn.setOnClickListener { navController.popBackStack() }
    }

    private fun setCharts(coinList: List<DoubleArray>) {
        val viewState = viewModel.currentState
        binding.chartView.aa_drawChartWithChartModel(
            Utils.getChartModel(
                viewState.coinDetail?.name ?: "",
                viewState.interval?.info ?: "Daily Price Change",
                coinList
            )
        )
    }

    override fun onChanged(timeInterval: TimeInterval) {
        viewModel.sendToEvent(CoinDetailViewEvent.OnSetTimeInterval(viewModel.uiState.value.copy(interval = timeInterval)))
    }
}