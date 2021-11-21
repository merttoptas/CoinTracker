package com.merttoptas.cointracker.features.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cointracker.utils.CoinDialogProgress

abstract class BaseFragment : Fragment() {
    private var progressBar: CoinDialogProgress? = null
    private var navController: NavController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressBar()
        navController = Navigation.findNavController(requireView())
    }

    fun showProgress() {
        progressBar?.show()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun hideProgress() {
        progressBar?.dismiss()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun initProgressBar() {
        context?.let {
            progressBar = CoinDialogProgress(it)
        }
    }
}