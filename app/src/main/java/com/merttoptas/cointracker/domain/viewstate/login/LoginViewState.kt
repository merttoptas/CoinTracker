package com.merttoptas.cointracker.domain.viewstate.login

import android.util.Patterns
import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class LoginViewState(
    val isDirectToMain: Boolean? = null,
    val email: String? = null,
    val password: String? = null,
    val isLoading: Boolean = false,
    val emailState: Boolean = false,
    val passwordState: Boolean = false,
    val errorMessage: String? = null
) : IViewState {
    fun validFields(): String? {
        fun emailValid() = Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches().not()

        return when {
            emailValid() -> "Please enter your email"
            else -> null
        }
    }
}
