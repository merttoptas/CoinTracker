package com.merttoptas.cointracker.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.merttoptas.cointracker.R
import kotlin.math.floor

fun Fragment.hideKeyboard(targetView: View) {
    activity?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(targetView.windowToken, 0)
    }
}

fun Fragment.showSnackBar(fragment: Fragment, text: String, snackBarEnum: SnackBarEnum) {
    SnackBarBuilder(fragment, text, snackBarEnum).show()
}

fun Activity.showSnackBar(activity: Activity, text: String, snackBarEnum: SnackBarEnum) {
    SnackBarBuilder(activity, text, snackBarEnum).show()
}

@BindingAdapter("app:loadUrlImage")
fun ImageView.loadUrlImage(url: String?) {
    Glide.with(this).load(url).into(this)
}

@BindingAdapter("app:doubleToString")
fun doubleToString(textView: TextView, value: Double?) {
    value?.let {
        textView.text = "$ $it"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:percentToString")
fun percentToString(textView: TextView, value: Double?) {
    value?.let {
        textView.text = "% ${floor(value * 100) / 100}"
    }
    if (value.toString().contains("-")) {
        textView.setTextColor(textView.context.resources.getColor(R.color.red))
    } else {
        textView.setTextColor(textView.context.resources.getColor(R.color.dark_green))
    }
}

@BindingAdapter("app:percentStatusIV")
fun percentStatusIV(imageView: ImageView, value: Double?) {
    if (value.toString().contains("-")) {
        imageView.setBackgroundResource(R.drawable.ic_arrow_downward)
    } else {
        imageView.setBackgroundResource(R.drawable.ic_arrrow_upward)
    }
}