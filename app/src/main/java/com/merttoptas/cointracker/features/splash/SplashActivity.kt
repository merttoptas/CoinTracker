package com.merttoptas.cointracker.features.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.utils.Utils
import com.merttoptas.cointracker.MainActivity
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ActivitySplashBinding
import com.merttoptas.cointracker.features.login.LoginViewModel
import com.merttoptas.cointracker.features.loginandregister.LoginAndRegisterActivity
import com.merttoptas.cointracker.utils.NetworkConnection
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.helper.NavigationHelper
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
            SnackBarBuilder(
                this,
                "Lütfen internet bağlantınızı kontrol ediniz.",
                SnackBarEnum.ERROR
            ).show()
        }
    }

    private fun resumeApp() {
        lifecycleScope.launchWhenResumed {
            viewModel.viewEffect.collect {
                when (it) {
                    is SplashViewEffect.DirectToLoginAndRegister -> {
                        startLoginAndRegisterActivity()
                    }

                    is SplashViewEffect.DirectToCoinList -> {
                        startMainActivity()
                    }
                }
            }
        }
    }

    private fun startLoginAndRegisterActivity() {
        lifecycleScope.launch {
            delay(2000)
            NavigationHelper.startLoginAndRegisterActivity(applicationContext)
            finish()
        }
    }

    private fun startMainActivity() {
        lifecycleScope.launch {
            delay(2000)
            NavigationHelper.startMainActivity(applicationContext)
            finish()
        }
    }
}