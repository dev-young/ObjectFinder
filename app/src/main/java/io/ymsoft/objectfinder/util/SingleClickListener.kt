package io.ymsoft.objectfinder.util

import android.view.View
import timber.log.Timber

class SingleClickListener (
    private val action: (v: View) -> Unit,
    private val interval: Long = 300
) :
    View.OnClickListener {

    private var clickable = true
    // clickable 플래그를 이 클래스가 아니라 더 상위 클래스에 두면
    // 여러 뷰에 대한 중복 클릭 방지할 수 있다.

    override fun onClick(v: View?) {
        if (clickable) {
            clickable = false
            v?.run {
                postDelayed({
                    clickable = true
                }, interval)
                action.invoke(v)
            }
        } else {
            Timber.d("waiting for a while")
        }
    }
}