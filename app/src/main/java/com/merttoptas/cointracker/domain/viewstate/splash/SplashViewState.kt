package com.merttoptas.cointracker.domain.viewstate.splash

import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class SplashViewState(
    val isCheckUserLogin: Boolean? = null,
    val isLoading: Boolean = false,
    val geckoSays: String? = null,
) : IViewState