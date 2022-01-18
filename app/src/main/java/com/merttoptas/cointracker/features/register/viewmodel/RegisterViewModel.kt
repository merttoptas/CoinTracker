    package com.merttoptas.cointracker.features.register.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.merttoptas.cointracker.domain.usecase.register.RegisterUseCase
    import com.merttoptas.cointracker.domain.usecase.register.RegisterViewEvent
    import com.merttoptas.cointracker.domain.viewstate.base.ViewData
    import com.merttoptas.cointracker.domain.viewstate.base.ViewEventWrapper
    import com.merttoptas.cointracker.domain.viewstate.register.RegisterViewState
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.flow.*
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    @HiltViewModel
    class RegisterViewModel @Inject constructor(
        private val registerUseCase: RegisterUseCase,
    ) : ViewModel() {

        private val _uiEvent = MutableSharedFlow<ViewEventWrapper<RegisterViewEvent>>()
        val uiEvent: SharedFlow<ViewEventWrapper<RegisterViewEvent>> = _uiEvent

        private val _uiState = MutableStateFlow(registerUseCase.getInitialData())
        val uiState: StateFlow<RegisterViewState> = _uiState

        fun sendToEvent(event: RegisterViewEvent) {
            viewModelScope.launch {
                registerUseCase.invoke(ViewEventWrapper.PageEvent(event)).collect {
                    when (it) {
                        is ViewData.State -> _uiState.emit(it.data)
                        is ViewData.Event -> _uiEvent.emit(it.data)
                    }
                }
            }
        }
    }