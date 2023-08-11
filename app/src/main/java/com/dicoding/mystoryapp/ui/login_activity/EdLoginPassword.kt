package com.dicoding.mystoryapp.ui.login_activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R

class EdLoginPassword : AppCompatEditText {
    private lateinit var iconLock: Drawable
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
        bgEdText = ContextCompat.getDrawable(context, R.drawable.ed_pasword_custom) as Drawable
        iconLock = ContextCompat.getDrawable(context, R.drawable.ic_lock_green) as Drawable

        showLockLogo()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val stringPassword = s.toString()

                if (stringPassword.isNotEmpty() && stringPassword.length < 8) {
                    error = "Password Minimum Length is 8"
                } else {
                    error = null
                }

            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun showLockLogo() {
        setButtonDrawables(startOfTheText = iconLock)
    }

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
        hint = "Input Your Password"
        textSize = 14f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}