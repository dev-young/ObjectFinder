package io.ymsoft.objectfinder.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private var measureListener : OnMeasureListener? = null
    fun setOnMeasureListener(measureListener: OnMeasureListener?){
        this.measureListener = measureListener
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
        measureListener?.measured(width, width)
    }

    interface OnMeasureListener{
        fun measured(width:Int, height:Int)
    }
}