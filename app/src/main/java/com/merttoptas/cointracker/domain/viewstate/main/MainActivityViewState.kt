package com.merttoptas.cointracker.domain.viewstate.main

import com.merttoptas.cointracker.domain.viewstate.base.IViewState

data class MainActivityViewState(
    val isDirectToLogin: Boolean? = null
) : IViewState