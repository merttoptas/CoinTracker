package com.merttoptas.cointracker.features.login

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    BaseViewModel<LoginViewState, LoginViewEffect>() {

    private val coroutineScope = MainScope()

    fun emailChange(value: String) {
        setState { currentState.copy(email = value) }
    }

    fun passwordChange(value: String) {
        setState { currentState.copy(password = value) }
    }

    fun login() {
        viewModelScope.launch {
            setState { currentState.copy(isLoading = true) }
            currentState.validFields()?.let {
                setState { currentState.copy(errorMessage = it.toString()) }
            } ?: kotlin.run {
                firebaseAuth.signInWithEmailAndPassword(
                    currentState.email ?: "",
                    currentState.password ?: ""
                ).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            setEffect(LoginViewEffect.SuccessfullyLogin)
                            setState { currentState.copy(isLoading = false) }
                        } else {
                            setEffect(LoginViewEffect.FailedLogin(task.exception?.message))
                            setState { currentState.copy(isLoading = false) }
                        }
                    }
                })
            }
        }
    }

    override fun createInitialState() = LoginViewState()
}

data class LoginViewState(
    val email: String? = null,
    val password: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState {
    fun validFields(): String? {
        fun passwordValid() = password.isNullOrEmpty()
        fun emailValid() = Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches().not()

        return when {
            passwordValid() -> "Please enter your password"
            emailValid() -> "Please enter your email<"
            else -> null
        }
    }
}

sealed class LoginViewEffect : IViewEffect {
    object SuccessfullyLogin : LoginViewEffect()
    class FailedLogin(val errorMessage: String?) : LoginViewEffect()
}