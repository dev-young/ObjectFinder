package io.ymsoft.objectfinder

import android.app.Application
import io.ymsoft.objectfinder.data.source.DefaultStorageModelsRepo
import io.ymsoft.objectfinder.data.source.StorageModelsRepository
import timber.log.Timber

class MyApp : Application() {
    val storageModelsRepository : StorageModelsRepository
        get() = DefaultStorageModelsRepo(this)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }


}