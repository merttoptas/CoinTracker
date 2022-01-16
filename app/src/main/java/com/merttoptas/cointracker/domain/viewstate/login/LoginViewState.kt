package com.merttoptas.cointracker.domain.viewstate.login

import android.util.Patterns
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class LoginViewState(
    val isDirectToMain: Boolean? = null,
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
