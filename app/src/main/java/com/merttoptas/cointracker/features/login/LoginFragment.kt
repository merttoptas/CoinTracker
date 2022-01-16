package com.merttoptas.cointracker.features.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentLoginBinding
import com.merttoptas.cointracker.domain.usecase.login.LoginViewEvent
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.login.viewmodel.LoginViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.helper.NavigationHelper
import com.merttoptas.cointracker.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val loginViewModel by viewModels<LoginViewModel>()
    override val layoutId: Int = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            navigate(R.id.action_loginFragment_to_registerFragment)
        }

        lifecycleScope.launchWhenResumed {
            launch {
                loginViewModel.uiState.collect {
                    if (it.isLoading) showProgress() else hideProgress()

                    it.errorMessage?.let {
                        SnackBarBuilder(
                            this@LoginFragment,
                            it,
                            SnackBarEnum.ERROR
                        ).show()
                    }

                    if (it.isDirectToMain == true) {
                        SnackBarBuilder(
                            this@LoginFragment,
                            "Succesfully",
                            SnackBarEnum.SUCCESS
                        ).show()
                        NavigationHelper.startMainActivity(
                            requireActivity(),
                            requireContext(),
                            false
                        )
                    }
                }
            }
        }

        onLogin()
    }

    private fun onLogin(){
        binding.btnLogin.setOnClickListener {
            binding.etEmail.setOnFocusChangeListener { view, focus ->
                if (focus) {
                    hideKeyboard(view)
                }
            }
            binding.etPassword.setOnFocusChangeListener { view, focus ->
                if (focus) {
                    hideKeyboard(view)
                }
            }

            loginViewModel.sendToEvent(
                LoginViewEvent.OnLoginEvent(
                    loginViewModel.uiState.value.copy(
                        email = binding.etEmail.text.trim().toString(),
                        password = binding.etPassword.text.trim().toString()
                    )
                )
            )
        }
    }
}