package com.merttoptas.cointracker.features.coindetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.data.model.TimeInterval
import com.merttoptas.cointracker.databinding.FragmentCoinDetailBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.adapter.OnClickListener
import com.merttoptas.cointracker.features.coindetail.adapter.TimeIntervalAdapter
import com.merttoptas.cointracker.features.coindetail.viewmodel.CoinDetailViewEffect
import com.merttoptas.cointracker.features.coindetail.viewmodel.CoinDetailViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.Utils
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
                viewModel.viewState.collect {
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
                viewModel.viewEffect.collect {
                    when (it) {
                        is CoinDetailViewEffect.Failed -> {
                            SnackBarBuilder(
                                this@CoinDetailFragment,
                                it.errorMessage.toString(),
                                SnackBarEnum.ERROR
                            ).show()
                        }
                        is CoinDetailViewEffect.StatusFavorite -> {
                            if (it.status.not()) {
                                SnackBarBuilder(
                                    this@CoinDetailFragment,
                                    it.errorMessage.toString(),
                                    SnackBarEnum.ERROR
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        binding.favouriteImageView.setOnClickListener {
            viewModel.updateFavoriteCoin()
        }

        binding.btnRefreshInterval.setOnClickListener {
            viewModel.refreshIntervalChange(binding.etRefreshInterval.text.toString().toInt())
            viewModel.setRefreshInterval()
        }
        binding.backBtn.setOnClickListener { navController.popBackStack() }
    }

    private fun setCharts(coinList: List<DoubleArray>) {
        val viewState = viewModel.currentState
        binding.aaChartView.aa_drawChartWithChartModel(
            Utils.getChartModel(
                viewState.coinDetail?.name ?: "",
                viewState.interval?.info ?: "Daily Price Change",
                coinList
            )
        )
    }

    override fun onChanged(timeInterval: TimeInterval) {
        viewModel.timeIntervalChange(timeInterval)
        viewModel.updateTimeInterval()

    }
}