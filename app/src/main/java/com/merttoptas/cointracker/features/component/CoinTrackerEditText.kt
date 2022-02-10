package com.merttoptas.cointracker.features.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.merttoptas.cointracker.R
import com.merttoptas.cointracker.databinding.ComponentCoinEditTextBinding

class CoinTrackerEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var binding: ComponentCoinEditTextBinding =
        ComponentCoinEditTextBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CoinTrackerEditText)
        binding.coinTrackerEditText.hint =
            attributes.getString(R.styleable.CoinTrackerEditText_hint_text)

        val inputType = attributes.getInt(
            R.styleable.CoinTrackerEditText_android_inputType,
            EditorInfo.TYPE_CLASS_TEXT
        )

        val maxLines = attributes.getInt(R.styleable.CoinTrackerEditText_maxLines, 1)
        val maxLength = attributes.getInt(R.styleable.CoinTrackerEditText_maxLength, 100)


        binding.coinTrackerEditText.inputType = inputType
        binding.coinTrackerEditText.setLines(maxLines)
        binding.coinTrackerEditText.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    fun getText() = binding.coinTrackerEditText.text

    fun setText(value: String) = binding.coinTrackerEditText.setText(value)

    fun getEditText(): EditText = binding.coinTrackerEditText


    fun setHintText(hintText: String?) {
        binding.coinTrackerEditText.hint = hintText
    }

    fun setStartIcon(icon: Drawable?) {
        binding.coinTrackerEditText.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:setEditTextHint")
        fun setEditTextHint(
            view: CoinTrackerEditText?,
            hintText: String?
        ) {
            view?.setHintText(hintText)
        }

        @JvmStatic
        @BindingAdapter("app:setEditStartIcon")
        fun setEditStartIcon(
            view: CoinTrackerEditText?,
            icon: Drawable?,
        ) {
            view?.setStartIcon(icon)
        }
    }
}