package com.merttoptas.cointracker.features.component

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ComponentCoinButtonBinding

class CoinTrackerButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ComponentCoinButtonBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.component_coin_button,
        this,
        true
    )

    fun setButtonText(buttonText: String?) {
        binding.coinTrackerButton.text = buttonText
    }

    fun isEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    companion object {
        @JvmStatic
        @BindingAdapter(
            value = ["app:isButtonActive", "app:setButtonText"],
            requireAll = false
        )
        fun setButton(
            view: CoinTrackerButton,
            isButtonActive: Boolean,
            buttonText: String?
        ) {
            if (!buttonText.isNullOrEmpty())
                view.setButtonText(buttonText)

            view.isEnabled(isButtonActive)
            view.binding.coinTrackerButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    view.context,
                    if (isButtonActive) R.color.light_red_orange else R.color.light_grayish_blue
                )
            )
            view.isClickable = isButtonActive.not()
        }
    }
}