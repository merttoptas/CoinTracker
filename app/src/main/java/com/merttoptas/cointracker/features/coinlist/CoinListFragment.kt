package com.merttoptas.cointracker.features.coinlist

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentCoinListBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.features.coinlist.adapter.CoinListAdapter
import com.merttoptas.cointracker.features.coinlist.adapter.OnClickListener
import com.merttoptas.cointracker.features.coinlist.viewmodel.CoinListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListFragment : BaseFragment<FragmentCoinListBinding>(), OnClickListener {

    private val loginViewModel by viewModels<CoinListViewModel>()
    override val layoutId: Int = R.layout.fragment_coin_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }
    }

    override fun onClick(id: String) {
        navigate(R.id.action_coinListFragment_to_coinDetailFragment, Bundle().apply {
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