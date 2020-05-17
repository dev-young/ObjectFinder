package io.ymsoft

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.ymsoft.objectfinder.repository.ObjectRepository

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ObjectRepository.setContext(this)
    }


}