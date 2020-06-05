package io.ymsoft.objectfinder.view_custom

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class MaxHeightLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var maxHeight = 120 * context.resources.displayMetrics.density.toInt()

    private var measureListener : ((Int, Int) -> Unit)? = null
    fun setOnMeasureListener(measureListener: ((Int, Int) -> Unit)?){
        this.measureListener = measureListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (measuredHeight > maxHeight){
            setMeasuredDimension(measuredWidth, maxHeight)
        }
        measureListener?.invoke(measuredWidth, measuredHeight)
    }

}