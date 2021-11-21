package com.merttoptas.cointracker.features.register

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStoreManager: DataStoreManager
) :
    BaseViewModel<RegisterViewState, RegisterViewEffect>() {

    private val coroutineScope = MainScope()

    fun emailChange(value: String) {
        setState { currentState.copy(email = value) }
    }

    fun passwordChange(value: String) {
        setState { currentState.copy(password = value) }
    }

    fun confirmPasswordChange(value: String) {
        setState { currentState.copy(confirmPassword = value) }
    }

    fun register() {
        viewModelScope.launch {
            setState { currentState.copy(isLoading = true) }
            firebaseAuth.createUserWithEmailAndPassword(
                currentState.email ?: "",
                currentState.password ?: ""
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setState { currentState.copy(isLoading = false) }
                        setEffect(RegisterViewEffect.SuccessfullyRegister)
                        viewModelScope.launch { dataStoreManager.updateUserLogin(true) }
                    } else {
                        setEffect(RegisterViewEffect.FailedRegister)
                        setState {
                            currentState.copy(
                                isLoading = false,
                                errorMessage = task.exception?.message
                            )
                        }
                    }

                }

            currentState.validFields()?.let {
                setState { currentState.copy(errorMessage = it.toString(), isLoading = false) }
            } ?: kotlin.run {

            }


        }
    }

    override fun createInitialState() = RegisterViewState()
}

data class RegisterViewState(
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : IViewState {
    fun validFields(): String? {
        fun passwordValid() = password?.count() ?: 0 in 6..12
        fun confirmPasswordValid() = confirmPassword?.count() ?: 0 in 6..12
        fun valid() = password != confirmPassword
        fun emailValid() = Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches().not()

        return when {
            passwordValid() -> "Password must be at least 6 characters."
            confirmPasswordValid() -> "Password must be at least 6 characters."
            valid() -> "Passwords are not equal"
            emailValid() -> "Please enter your email"
            else -> null
        }
    }
}

sealed class RegisterViewEffect : IViewEffect {
    object SuccessfullyRegister : RegisterViewEffect()
    object FailedRegister : RegisterViewEffect()
}