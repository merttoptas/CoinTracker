package com.merttoptas.cointracker.features.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ActivityMainBinding
import com.merttoptas.cointracker.features.mainactivity.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val DIRECT_TO_LOGIN = "DIRECT_TO_LOGIN"
    }

    private var navController: NavController? = null
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect { viewState ->
                    binding.isVisibleBottomBar = viewState.isDirectToLogin
                    initNavController(viewState.isDirectToLogin ?: false)
                }
            }
        }
    }

    private fun initNavController(isDirectLogin: Boolean) {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(if (isDirectLogin) R.id.loginFragment else R.id.coinListFragment, true)
            .build()
        navController?.let {
            it.navigate(
                if (isDirectLogin) R.id.loginFragment else R.id.coinListFragment,
                null,
                navOptions
            )
            binding.bottomNavigation.setupWithNavController(it)
        }
    }
}