package io.ymsoft.objectfinder.util

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


object PointerUtil {

    fun movePointer(pointer: View, parent: ViewGroup, x: Float, y: Float) {
        if(x < 0 || y < 0 || x > parent.width || y > parent.height)
            return
        pointer.x = x - (pointer.pivotX)
        pointer.y = y - (pointer.pivotY)

    }

    /**상대위치를 통해 포인터 마진값 수정
     * 위치를 찾을 수 없을 경우 뷰의 visibility 를 GONE 으로 설정한다. */
    @SuppressLint("CheckResult")
    fun movePointerByRelative(pointer: View, parent: View, rx: Float?, ry: Float?): Boolean {
        if(rx == null || ry == null || rx < 0 || ry < 0 || rx > 100 || ry > 100){
            pointer.visibility = View.GONE
            return false
        }

        var parentWidth = parent.width
        var parentHeight = parent.height
        var pointerX: Float //포인터가 위차할 x좌표
        var pointerY: Float //포인터가 위차할 y좌표

        if(parentWidth > 0 && parentHeight > 0){
            pointerX = (parentWidth * rx) - (pointer.pivotX)
            pointerY = (parentHeight * ry) - (pointer.pivotY)
        } else {
            ViewUtil.measure(parent)
            ViewUtil.measure(pointer)
            parentWidth = parent.measuredWidth
            parentHeight = parent.measuredHeight

            if(parentWidth > 0 && parentHeight > 0){
                pointerX = (parentWidth * rx) - (pointer.measuredWidth/2)
                pointerY = (parentHeight * ry) - (pointer.measuredHeight/2)
            } else {
                //  measure 를 사용해도 값이 0인 경우 최후의 수단으로 딜레이를 준 뒤에 계산한다.
                Observable.just(Pair(rx, ry))
                    .delay(50, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        val x = parent.width * rx
                        val y = parent.height * ry

                        pointer.x = x - (pointer.pivotX)
                        pointer.y = y - (pointer.pivotY)

                        pointer.visibility = View.VISIBLE
                        logE("딜레이 사용")
                    }
                return true
            }
        }

        pointer.x = pointerX
        pointer.y = pointerY
        pointer.visibility = View.VISIBLE
        return true
    }

    fun movePointerByRelative(pointer: View, parentWidth: Int, parentHeight: Int, rx: Float?, ry: Float?): Boolean {
        if(rx == null || ry == null || rx < 0 || ry < 0 || rx > 100 || ry > 100){
            pointer.visibility = View.GONE
            return false
        }

        val x = parentWidth * rx
        val y = parentHeight * ry
        var pivotX = pointer.pivotX
        var pivotY = pointer.pivotY

        if(pivotX == 0f || pivotX == 0f){
            ViewUtil.measure(pointer)
            pivotX = (pointer.measuredWidth/2).toFloat()
            pivotY = (pointer.measuredHeight/2).toFloat()
        }

        if(pivotX > 0 && pivotX > 0){
            pointer.x = x - (pivotX)
            pointer.y = y - (pivotY)
            pointer.visibility = View.VISIBLE

        }
        return true
    }
}