package com.merttoptas.cointracker.features.myfavorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentMyFavoriteBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.coindetail.CoinDetailFragment
import com.merttoptas.cointracker.features.myfavorite.adapter.MyFavoriteAdapter
import com.merttoptas.cointracker.features.myfavorite.adapter.OnClickListener
import com.merttoptas.cointracker.features.myfavorite.viewmodel.MyFavoriteViewEffect
import com.merttoptas.cointracker.features.myfavorite.viewmodel.MyFavoriteViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFavoriteFragment : BaseFragment<FragmentMyFavoriteBinding>(), OnClickListener {

    private val viewModel by viewModels<MyFavoriteViewModel>()
    override val layoutId: Int = R.layout.fragment_my_favorite

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {

            launch {
                viewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()

                    binding.rvFavoriteList.adapter =
                        MyFavoriteAdapter(this@MyFavoriteFragment).apply {
                            submitList(it.coinList)
                        }
                }
            }
            launch {
                viewModel.viewEffect.collect {
                    when (it) {
                        is MyFavoriteViewEffect.Failed -> {
                            it.errorMessage?.let { safeMessage ->
                                SnackBarBuilder(
                                    this@MyFavoriteFragment,
                                    safeMessage,
                                    SnackBarEnum.ERROR
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onClick(id: String) {
        navigate(R.id.action_myFavoriteFragment_to_coinDetailFragment, Bundle().apply {
            putString(CoinDetailFragment.COIN_ID, id)
        })
    }
}