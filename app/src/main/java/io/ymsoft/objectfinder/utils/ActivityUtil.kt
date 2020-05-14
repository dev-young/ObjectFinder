package io.ymsoft.objectfinder.utils

import android.content.Context
import android.content.Intent

class ActivityUtil {
    companion object {
        fun <T> start(from: Context, to: Class<T>){
            from.startActivity(Intent(from, to))
        }
    }
}