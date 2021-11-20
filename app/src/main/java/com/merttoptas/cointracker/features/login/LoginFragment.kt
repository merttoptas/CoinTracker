package com.merttoptas.cointracker.features.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.FragmentLoginBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import com.merttoptas.cointracker.utils.SnackBarBuilder
import com.merttoptas.cointracker.utils.SnackBarEnum
import com.merttoptas.cointracker.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val loginViewModel by viewModels<LoginViewModel>()
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireView())


        binding.tvRegister.setOnClickListener {
            navController?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        lifecycleScope.launchWhenResumed {
            launch {
                loginViewModel.viewState.collect {
                    if (it.isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                loginViewModel.viewEffect.collect { effect ->
                    when (effect) {
                        is LoginViewEffect.SuccessfullyLogin -> {
                            SnackBarBuilder(
                                this@LoginFragment,
                                "Succesfully",
                                SnackBarEnum.ERROR
                            ).show()

                        }
                        is LoginViewEffect.FailedLogin -> {
                            effect.errorMessage?.let {
                                SnackBarBuilder(
                                    this@LoginFragment,
                                    effect.errorMessage,
                                    SnackBarEnum.ERROR
                                ).show()
                            }
                            Log.d("deneme1", effect.errorMessage.toString())

                        }
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            loginViewModel.emailChange(binding.etEmail.text.trim().toString())
            loginViewModel.passwordChange(binding.etPassword.text.trim().toString())

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
            loginViewModel.login()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}