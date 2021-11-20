package com.merttoptas.cointracker.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.merttoptas.cointracker.R

class SnackBarBuilder(fragment: Fragment, text: String, snackBarEnum: SnackBarEnum) {
    private var snackBar: Snackbar ?= null

    init {
        createSnackBar(fragment.view, text, snackBarEnum)
    }

    private fun createSnackBar(targetView: View?, text: String, snackBarEnum: SnackBarEnum) {
        targetView?.let {  targetView ->
            targetView.context?.let { safeContext ->
                with(Snackbar.make(targetView, text, Snackbar.LENGTH_LONG)) {
                    val snackBarTextView: TextView = view.findViewById(R.id.snackbar_text)
                    snackBarTextView.setTextColor(ContextCompat.getColor(safeContext, snackBarEnum.textColor))
                    view.setBackgroundColor(ContextCompat.getColor(safeContext, snackBarEnum.backgroundColor))
                    snackBar = this
                }
            }
        }

    }
    fun show() {
        snackBar?.show()
    }
}

sealed class SnackBarEnum( @ColorRes val backgroundColor: Int, @ColorRes val textColor: Int = R.color.white) {
    object SUCCESS : SnackBarEnum(R.color.dark_green)
    object ERROR : SnackBarEnum(R.color.red)
}