package com.merttoptas.cointracker.features.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentRegisterBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.register.viewmodel.RegisterViewEffect
import com.merttoptas.cointracker.features.register.viewmodel.RegisterViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val registerViewModel by viewModels<RegisterViewModel>()
    override val layoutId: Int = R.layout.fragment_register

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnRegister.setOnClickListener {
                registerViewModel.emailChange(etEmail.text.trim().toString())
                registerViewModel.passwordChange(etPassword.text.trim().toString())
                registerViewModel.confirmPasswordChange(etConfirmPassword.text.trim().toString())

                registerViewModel.register()
            }
        }

        lifecycleScope.launchWhenResumed {
            launch {
                registerViewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                registerViewModel.viewEffect.collect { effect ->
                    when (effect) {
                        is RegisterViewEffect.SuccessfullyRegister -> {
                            navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is RegisterViewEffect.FailedRegister -> {
                            effect.errorMessage?.let {
                                SnackBarBuilder(
                                    this@RegisterFragment,
                                    effect.errorMessage,
                                    SnackBarEnum.ERROR
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}