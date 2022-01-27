package com.merttoptas.cointracker.features.splash

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ActivitySplashBinding
import com.merttoptas.cointracker.utils.NetworkConnection
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.helper.NavigationHelper
import com.merttoptas.cointracker.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val viewModel by viewModels<SplashViewModel>()

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAnimation()
        startApp()
    }

    private fun initAnimation() {
        binding.ivCoinLogo.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.tvBitcoin.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_animation)
    }

    private fun startApp() {
        if (NetworkConnection.isNetworkAvailable(applicationContext)) {
            resumeApp()
        } else {
            showSnackBar(
                this,
                getString(R.string.internet_connection_failed_text),
                SnackBarEnum.ERROR
            )

            lifecycleScope.launch {
                delay(3000)
                finishAffinity()
            }
        }
    }

    private fun resumeApp() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiState.collect { viewState ->
                if (viewState.isCheckUserLogin == true) {
                    startMainActivity(false)
                } else if (viewState.isCheckUserLogin == false) {
                    startMainActivity(true)
                }
            }
        }
    }

    private fun startMainActivity(isDirectToLogin: Boolean) {
        lifecycleScope.launch {
            delay(2000)
            NavigationHelper.startMainActivity(
                this@SplashActivity,
                applicationContext,
                isDirectToLogin
            )
            finish()
        }
    }
}