package com.merttoptas.cointracker.features.coinlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentCoinListBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coinlist.viewmodel.CoinListViewEffect
import com.merttoptas.cointracker.features.coinlist.viewmodel.CoinListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListFragment : BaseFragment() {

    private val loginViewModel by viewModels<CoinListViewModel>()
    private var _binding: FragmentCoinListBinding? = null

    private val binding get() = _binding!!

    private var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireView())

        lifecycleScope.launchWhenResumed {
            launch {
                loginViewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                loginViewModel.viewEffect.collect { effect ->
                    when (effect) {
                        is CoinListViewEffect.SuccessfullyLogin -> {
                        }
                        is CoinListViewEffect.FailedLogin -> {
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}