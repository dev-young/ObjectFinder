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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        logI("측정된 높이: $measuredHeight  -> $text")
        val lines = measuredHeight/lineSpace
        if(maxLines != lines){
            ellipsize = null
            maxLines = lines
            ellipsize = TextUtils.TruncateAt.END
//            logI("수정된 줄 수 : $lines  -> $text")
        }
    }



}