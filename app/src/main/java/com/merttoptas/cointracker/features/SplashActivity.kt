package com.merttoptas.cointracker.features

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.MainActivity
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAnimation()

        lifecycleScope.launch {
            delay(5000)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initAnimation() {
        binding.ivCoinLogo.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.top_animation)
        binding.tvBitcoin.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_animation)
    }
}