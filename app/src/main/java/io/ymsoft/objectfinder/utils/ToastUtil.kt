package io.ymsoft.objectfinder.utils

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object{
        fun s(context:Context?, msg:String){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun l(context:Context?, msg:String){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}