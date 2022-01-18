package com.merttoptas.cointracker.domain.usecase.register

import com.merttoptas.cointracker.data.local.DataStoreManager
import com.merttoptas.cointracker.data.remote.service.FirebaseService
import com.merttoptas.cointracker.domain.usecase.IUseCase
import com.merttoptas.cointracker.domain.viewstate.base.IViewEvent
import com.merttoptas.cointracker.domain.viewstate.base.ViewData
import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
import com.merttoptas.cointracker.domain.viewstate.register.RegisterViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
    private val dataStoreManager: DataStoreManager,
    private val firebaseService: FirebaseService,
) : IUseCase<RegisterViewEvent, RegisterViewState> {
    override fun getInitialData(event: RegisterViewEvent?): RegisterViewState {
        return RegisterViewState()
    }

    override fun invoke(event: ViewEventWrapper<RegisterViewEvent>) = flow {
        if (event is ViewEventWrapper.PageEvent && event.pageEvent is RegisterViewEvent.OnLoginEvent) {
            emitAll(register(event.pageEvent.viewState))
        }
    }

    private fun register(viewState: RegisterViewState) =
        flow<ViewData<RegisterViewState, RegisterViewEvent>> {
            emit(ViewData.State(viewState.copy(isLoading = true)))
            viewState.validFields()?.let {
                emit(ViewData.State(viewState.copy(isLoading = false)))
                emit(
                    ViewData.Event(
                        ViewEventWrapper.PageEvent(RegisterViewEvent.SnackBarError(
                            it))
                    )
                )
            } ?: kotlin.run {
                firebaseService.register(viewState.email ?: "", viewState.password ?: "")
                    .collect { registerData ->
                        if (registerData.result) {
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
                                    ViewEventWrapper.PageEvent(RegisterViewEvent.SuccessfullyRegister)
                                )
                            )
                        } else {
                            emit(
                                ViewData.State(
                                    viewState.copy(
                                        isLoading = false
                                    )
                                )
                            )
                            emit(
                                ViewData.Event(
                                    ViewEventWrapper.PageEvent(RegisterViewEvent.SnackBarError(
                                        registerData.error))
                                )
                            )
                        }
                    }
            }

        }.flowOn(defaultDispatcher)
}

sealed class RegisterViewEvent : IViewEvent {
    class OnLoginEvent(val viewState: RegisterViewState) : RegisterViewEvent()
    object SuccessfullyRegister : RegisterViewEvent()
    class SnackBarError(val message: String?) : RegisterViewEvent()
}