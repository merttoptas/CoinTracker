package com.merttoptas.cointracker.domain.usecase.login

import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.login.LoginViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val dataStoreManager: DataStoreManager,
    private val firebaseService: FirebaseService,
) : IUseCase<LoginViewEvent, LoginViewState> {
    override fun getInitialData(event: LoginViewEvent?): LoginViewState {
        return LoginViewState()
    }

    override fun invoke(event: ViewEventWrapper<LoginViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is LoginViewEvent.OnLoginEvent) {
            emitAll(login(event.pageEvent.viewState))
        }
    }

    private fun login(viewState: LoginViewState) = flow<ViewData<LoginViewState, LoginViewEvent>> {
        emit(ViewData.State(viewState.copy(isLoading = true)))
        viewState.validFields()?.let {
            emit(ViewData.State(viewState.copy(isLoading = false)))
            emit(
                ViewData.Event(
                    ViewEventWrapper.PageEvent(LoginViewEvent.SnackBarError(
                        it))
                )
            )
        } ?: kotlin.run {
            firebaseService.login(viewState.email ?: "", viewState.password ?: "")
                .collect { loginData ->
                    if (loginData.result) {
                        dataStoreManager.updateUserLogin(true)
                        emit(
                            ViewData.State(
                                viewState.copy(
                                    isLoading = false
                                )
                            )
                        )
                        emit(
                            ViewData.Event(
                                ViewEventWrapper.PageEvent(LoginViewEvent.SuccessfullyLogin)
                            )
                        )
                    } else {
                        emit(
                            ViewData.State(
                                viewState.copy(
                                    isLoading = false,
                                    errorMessage = loginData.error
                                )
                            )
                        )
                    }
                }
        }
    }.flowOn(defaultDispatcher)

}

sealed class LoginViewEvent : IViewEvent {
    class OnLoginEvent(val viewState: LoginViewState) : LoginViewEvent()
    object SuccessfullyLogin : LoginViewEvent()
    class SnackBarError(val message: String?) : LoginViewEvent()
}