package com.merttoptas.cointracker.features.loginandregister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.merttoptas.cointracker.databinding.ActivityLoginAndRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginAndRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAndRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAndRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}