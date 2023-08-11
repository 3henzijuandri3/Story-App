package com.dicoding.mystoryapp.ui.register_activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R

class EdRegisterEmail : AppCompatEditText {

    private lateinit var iconMail: Drawable
    private lateinit var bgEdText: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iconMail = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        bgEdText = ContextCompat.getDrawable(context, R.drawable.ed_pasword_custom) as Drawable

        showEmailLogo()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val stringEmail = s.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches() == false) error =
                    "Invalid Email Format" else error = null
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }

        })
    }

    private fun showEmailLogo() = setButtonDrawables(startOfTheText = iconMail)

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = bgEdText
        hint = "Input Your Email"
        textSize = 14f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}