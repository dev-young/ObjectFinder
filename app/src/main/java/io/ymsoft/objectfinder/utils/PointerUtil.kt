package io.ymsoft.objectfinder.utils

import android.view.View
import android.view.ViewGroup


object PointerUtil {

    fun movePointer(pointer: View, parent: ViewGroup, x: Float, y: Float) {
        if(x < 0 || y < 0 || x > parent.width || y > parent.height)
            return
        pointer.x = x - (pointer.pivotX)
        pointer.y = y - (pointer.pivotY)

    }

    /**상대위치를 통해 포인터 마진값 수정*/
    fun movePointerByRelative(pointer: View, parent: View, rx: Float, ry: Float) {
        if(rx < 0 || ry < 0 || rx > 100 || ry > 100)
            return

        val x = parent.width * rx
        val y = parent.height * ry

        pointer.x = x - (pointer.pivotX)
        pointer.y = y - (pointer.pivotY)
    }
}