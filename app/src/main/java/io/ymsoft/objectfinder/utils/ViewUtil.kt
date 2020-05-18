package io.ymsoft.objectfinder.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout

object ViewUtil {
    fun measure(view: View) {
        val layoutParams = view.layoutParams
        val horizontalMode: Int
        val horizontalSize: Int
        when (layoutParams.width) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                horizontalMode = View.MeasureSpec.EXACTLY
                horizontalSize = if (view.parent is LinearLayout
                    && (view.parent as LinearLayout).orientation == LinearLayout.VERTICAL
                ) {
                    val lp = view.layoutParams as MarginLayoutParams
                    (view.parent as View).measuredWidth - lp.leftMargin - lp.rightMargin
                } else {
                    (view.parent as View).measuredWidth
                }
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                horizontalMode = View.MeasureSpec.UNSPECIFIED
                horizontalSize = 0
            }
            else -> {
                horizontalMode = View.MeasureSpec.EXACTLY
                horizontalSize = layoutParams.width
            }
        }
        val horizontalMeasureSpec = View.MeasureSpec
            .makeMeasureSpec(horizontalSize, horizontalMode)
        val verticalMode: Int
        val verticalSize: Int
        when (layoutParams.height) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                verticalMode = View.MeasureSpec.EXACTLY
                verticalSize = if (view.parent is LinearLayout
                    && (view.parent as LinearLayout).orientation == LinearLayout.HORIZONTAL
                ) {
                    val lp = view.layoutParams as MarginLayoutParams
                    (view.parent as View).measuredHeight - lp.topMargin - lp.bottomMargin
                } else {
                    (view.parent as View).measuredHeight
                }
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                verticalMode = View.MeasureSpec.UNSPECIFIED
                verticalSize = 0
            }
            else -> {
                verticalMode = View.MeasureSpec.EXACTLY
                verticalSize = layoutParams.height
            }
        }
        val verticalMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(verticalSize, verticalMode)
        view.measure(horizontalMeasureSpec, verticalMeasureSpec)
    }
}