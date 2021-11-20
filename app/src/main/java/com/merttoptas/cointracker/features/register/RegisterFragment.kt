package com.merttoptas.cointracker.features.register

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
import com.merttoptas.cointracker.databinding.FragmentRegisterBinding
import com.merttoptas.cointracker.features.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {

    private val registerViewModel by viewModels<RegisterViewModel>()
    private var navController: NavController? = null
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireView())

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
                            navController?.navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is RegisterViewEffect.FailedRegister -> {
                            Log.d("deneme1", "buraya girdi")
                        }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}