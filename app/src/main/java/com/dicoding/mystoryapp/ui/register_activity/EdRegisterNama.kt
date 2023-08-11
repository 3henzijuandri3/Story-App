package com.dicoding.mystoryapp.ui.register_activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R

class EdRegisterNama : AppCompatEditText {

    private lateinit var iconPerson: Drawable
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
        iconPerson = ContextCompat.getDrawable(context, R.drawable.ic_person) as Drawable
        bgEdText = ContextCompat.getDrawable(context, R.drawable.ed_pasword_custom) as Drawable

        showPersonLogo()
    }

    private fun showPersonLogo() = setButtonDrawables(startOfTheText = iconPerson)

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
        hint = "Input Your Name"
        textSize = 14f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}