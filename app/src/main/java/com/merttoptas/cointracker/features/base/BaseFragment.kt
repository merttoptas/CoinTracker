package com.merttoptas.cointracker.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.merttoptas.cointracker.utils.CoinDialogProgress

abstract class BaseFragment<binding : ViewDataBinding> : Fragment() {
    abstract val layoutId: Int

    private var progressBar: CoinDialogProgress? = null
    open lateinit var binding: binding
    protected val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (::binding.isInitialized) {
            return binding.root
        }
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressBar()
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

    fun navigate(actionId: Int, bundle: Bundle? = null) {
        navController.navigate(actionId, bundle)
    }
}