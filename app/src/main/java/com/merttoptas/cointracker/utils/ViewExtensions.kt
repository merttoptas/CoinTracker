package com.merttoptas.cointracker.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
    return this
}

fun View.remove(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun Fragment.hideKeyboard(targetView: View) {
    activity?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(targetView.windowToken, 0)
    }
}


@BindingAdapter("app:loadUrlImage")
fun ImageView.loadUrlImage(url: String?) {
    Glide.with(this).load(url).into(this)
}

@BindingAdapter("app:loadUrlRoundedImage")
fun ImageView.loadUrlRoundedImage(url: String?) {
    url?.let {
        Glide.with(this).asBitmap().load(url).apply(RequestOptions.bitmapTransform(RoundedCorners(10))).into(this)
    }
}
