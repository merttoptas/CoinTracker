package com.merttoptas.cointracker.features.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentRegisterBinding
import com.merttoptas.cointracker.domain.usecase.register.RegisterViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.features.register.viewmodel.RegisterViewModel
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val viewModel by viewModels<RegisterViewModel>()
    override val layoutId: Int = R.layout.fragment_register
    override var binding by autoCleared<FragmentRegisterBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnRegister.setOnClickListener {
                viewModel.sendToEvent(RegisterViewEvent.OnLoginEvent(viewModel.uiState.value.copy(
                    email = etEmail.text.trim().toString(),
                    password = etPassword.text.trim().toString(),
                    confirmPassword = etConfirmPassword.text.trim().toString(),
                )))
            }
        }

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.uiEvent.collect { event ->
                    if (event is ViewEventWrapper.PageEvent && event.pageEvent is RegisterViewEvent.SuccessfullyRegister) {
                        navigate(R.id.action_registerFragment_to_loginFragment)
                    } else if (event is ViewEventWrapper.PageEvent && event.pageEvent is RegisterViewEvent.SnackBarError) {
                        event.pageEvent.message?.let {
                            SnackBarBuilder(
                                this@RegisterFragment,
                                it,
                                SnackBarEnum.ERROR
                            ).show()
                        }
                    }
                }
            }
        }
    }
}