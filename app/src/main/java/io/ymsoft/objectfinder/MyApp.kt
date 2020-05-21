package io.ymsoft.objectfinder

import android.app.Application
import io.ymsoft.objectfinder.data.source.ObjectRepository

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ObjectRepository.setContext(this)
    }


}