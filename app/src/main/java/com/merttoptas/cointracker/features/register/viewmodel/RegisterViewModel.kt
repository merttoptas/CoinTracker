package com.merttoptas.cointracker.features.register.viewmodel

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.features.base.BaseViewModel
import com.merttoptas.cointracker.features.base.IViewEffect
import com.merttoptas.cointracker.features.base.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStoreManager: DataStoreManager,
    private val firebaseService: FirebaseService
) :
    BaseViewModel<RegisterViewState, RegisterViewEffect>() {

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
            currentState.validFields()?.let {
                setState { currentState.copy(isLoading = false) }
                setEffect(RegisterViewEffect.FailedRegister(errorMessage = it))
            } ?: kotlin.run {
                firebaseAuth.createUserWithEmailAndPassword(
                    currentState.email ?: "",
                    currentState.password ?: ""
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.uid?.let {
                                val data = HashMap<String, Any>()
                                data["email"] = currentState.email ?: ""
                                firebaseService.setUser(it, data)
                            }

                            setState { currentState.copy(isLoading = false) }
                            setEffect(RegisterViewEffect.SuccessfullyRegister)
                            viewModelScope.launch { dataStoreManager.updateUserLogin(true) }
                        } else {
                            setEffect(RegisterViewEffect.FailedRegister(task.exception?.message))
                            setState {
                                currentState.copy(isLoading = false)
                            }
                        }
                    }
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
    private fun checkFields() =
        email.isNullOrBlank() || password.isNullOrBlank() || confirmPassword.isNullOrBlank()

    private fun passwordValid() = password?.count() ?: 0 in 6..12
    private fun confirmPasswordValid() = confirmPassword?.count() ?: 0 in 6..12
    private fun valid() = password != confirmPassword
    private fun emailValid() = Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches().not()

    fun validFields(): String? {
        return when {
            checkFields() -> "Fill in the required fields."
            emailValid() -> "Please enter your email"
            passwordValid() -> "Password must be at least 6 characters."
            confirmPasswordValid() -> "Password must be at least 6 characters."
            !valid() -> "Passwords are not equal"
            else -> null
        }
    }
}

sealed class RegisterViewEffect : IViewEffect {
    object SuccessfullyRegister : RegisterViewEffect()
    class FailedRegister(val errorMessage: String?) : RegisterViewEffect()
}