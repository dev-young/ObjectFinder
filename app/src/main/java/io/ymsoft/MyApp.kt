package io.ymsoft

import android.app.Application
import io.ymsoft.objectfinder.repository.ObjectRepository

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ObjectRepository.setContext(this)
    }
}