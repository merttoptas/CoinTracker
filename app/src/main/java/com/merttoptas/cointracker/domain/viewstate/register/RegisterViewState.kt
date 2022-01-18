package com.merttoptas.cointracker.domain.viewstate.register

import android.util.Patterns
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

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