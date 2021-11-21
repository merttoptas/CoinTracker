package com.merttoptas.cointracker.features.coinlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentCoinListBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.features.coinlist.adapter.CoinListAdapter
import com.merttoptas.cointracker.features.coinlist.adapter.OnClickListener
import com.merttoptas.cointracker.features.coinlist.viewmodel.CoinListViewEffect
import com.merttoptas.cointracker.features.coinlist.viewmodel.CoinListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListFragment : BaseFragment(), OnClickListener {

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

        searchCoin()

        lifecycleScope.launchWhenResumed {
            launch {
                loginViewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()

                    binding.rvCoinList.adapter = CoinListAdapter(this@CoinListFragment).apply {
                        submitList(it.coinList)
                    }
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

    override fun onClick(id: String) {
        findNavController().navigate(R.id.action_coinListFragment_to_coinDetailFragment, Bundle().apply {
            putString(CoinDetailFragment.COIN_ID, id)
        })
    }

    private fun searchCoin() {
        binding.coinSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let {
                    loginViewModel.getFilterQueryData(p0)
                } ?: kotlin.run {
                    return true
                }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    binding.rvCoinList.adapter = CoinListAdapter(this@CoinListFragment).apply {
                        submitList(loginViewModel.currentState.filteredCoinList)
                    }
                    return true
                }
                return true
            }
        })
    }
}