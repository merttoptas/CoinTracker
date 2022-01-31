package com.merttoptas.cointracker.features.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentLoginBinding
import com.merttoptas.cointracker.domain.usecase.login.LoginViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.login.viewmodel.LoginViewModel
import com.merttoptas.cointracker.utils.*
import com.merttoptas.cointracker.utils.helper.NavigationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel by viewModels<LoginViewModel>()
    override val layoutId: Int = R.layout.fragment_login

    override var binding by autoCleared<FragmentLoginBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            navigate(R.id.action_loginFragment_to_registerFragment)
        }

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    if (it.isLoading) showProgress() else hideProgress()

                    it.errorMessage?.let {
                        showSnackBar(this@LoginFragment, it, SnackBarEnum.ERROR)
                    }
                    binding.isEnabled = it.emailState && it.passwordState
                    Log.d("deneme1",  it.toString())
                }
            }

            launch {
                viewModel.uiEvent.collect { event ->
                    if (event is ViewEventWrapper.PageEvent && event.pageEvent is LoginViewEvent.SuccessfullyLogin) {
                           NavigationHelper.startMainActivity(
                            requireActivity(),
                            requireContext(),
                            false
                        )
                    } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is LoginViewEvent.SnackBarError) {
                        event.pageEvent.message?.let {
                            showSnackBar(this@LoginFragment, it, SnackBarEnum.ERROR)
                        }
                    }
                }
            }
        }

        onLogin()

        binding.etEmail.doAfterTextChanged {
            viewModel.sendToEvent(
                LoginViewEvent.OnEnableButton(
                    viewModel.uiState.value.copy(
                        emailState = binding.etEmail.text.isNullOrEmpty().not(),
                    )
                )
            )
            binding.etPassword.doAfterTextChanged {
                viewModel.sendToEvent(
                    LoginViewEvent.OnEnableButton(
                        viewModel.uiState.value.copy(
                            passwordState = binding.etPassword.text.isNullOrEmpty().not(),
                        )
                    )
                )
            }
        }
    }

    private fun onLogin() {
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

            viewModel.sendToEvent(
                LoginViewEvent.OnLoginEvent(
                    viewModel.uiState.value.copy(
                        email = binding.etEmail.text.trim().toString(),
                        password = binding.etPassword.text.trim().toString()
                    )
                )
            )
        }
    }
}