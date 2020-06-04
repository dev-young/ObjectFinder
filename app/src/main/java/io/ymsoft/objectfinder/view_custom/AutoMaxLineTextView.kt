package io.ymsoft.objectfinder.view_custom

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import io.ymsoft.objectfinder.util.logI

class AutoMaxLineTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    private var measureListener : ((TextView, Int, Int) -> Unit)? = null
    fun setOnMeasureListener(measureListener: ((TextView, Int, Int) -> Unit)?){
        this.measureListener = measureListener
    }

    private val lineSpace by lazy { (paint.fontMetrics.bottom - paint.fontMetrics.top).toInt() }
    init {
        maxLines = 4
        ellipsize = TextUtils.TruncateAt.END
        viewTreeObserver.addOnGlobalLayoutListener {
            val h = height
            val lh = lineSpace
            val lines = h/lh
            if(maxLines != lines)
                maxLines = lines
//            logI("$adapterPosition -> $h, $lh, $lines")
            postInvalidate()
        }
    }



}