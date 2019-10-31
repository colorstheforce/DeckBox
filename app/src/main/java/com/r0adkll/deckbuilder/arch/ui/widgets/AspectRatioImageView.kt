package com.r0adkll.deckbuilder.arch.ui.widgets

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import com.r0adkll.deckbuilder.R
import kotlin.math.roundToInt

class AspectRatioImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var ratioType = RATIO_WIDTH

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView, defStyleAttr, 0)
        ratioType = a.getInt(R.styleable.AspectRatioImageView_ratioType, RATIO_WIDTH)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (drawable != null) {
            when (ratioType) {
                RATIO_WIDTH -> {
                    val width = MeasureSpec.getSize(widthMeasureSpec)
                    val height = (width * (drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth.toFloat())).roundToInt()
                    setMeasuredDimension(width, height)
                }
                RATIO_HEIGHT -> {
                    val height = MeasureSpec.getSize(heightMeasureSpec)
                    val width = (height * (drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat())).roundToInt()
                    setMeasuredDimension(width, height)
                }
                else -> super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    companion object {
        const val RATIO_NONE = -1
        const val RATIO_WIDTH = 0
        const val RATIO_HEIGHT = 1
    }
}
