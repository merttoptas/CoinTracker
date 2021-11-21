package com.merttoptas.cointracker.features.coindetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cointracker.databinding.FragmentCoinDetailBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.viewmodel.CoinDetailViewEffect
import com.merttoptas.cointracker.features.coindetail.viewmodel.CoinDetailViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinDetailFragment : BaseFragment() {

    companion object {
        const val COIN_ID = "COIN_ID"
    }

    private val viewModel by viewModels<CoinDetailViewModel>()
    private var _binding: FragmentCoinDetailBinding? = null

    private val binding get() = _binding!!
    private var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireView())

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                    binding.dataHolder = it.coinDetail
                    binding.isFavorite = it.isFavorite ?: false
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

        binding.floatActionFavoriteBtn.setOnClickListener {
            viewModel.updateFavoriteCoin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}