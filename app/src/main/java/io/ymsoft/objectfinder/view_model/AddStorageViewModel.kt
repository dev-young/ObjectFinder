package io.ymsoft.objectfinder.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.repository.ObjectRepository

class AddStorageViewModel(application: Application) : AndroidViewModel(application) {

    private val objectRepo = ObjectRepository
    val isWorking = objectRepo.isWorking  //백그라운드 작업중인지 여부






    fun addNew(storageModel:StorageModel? = null, objectModel: ObjectModel, listener: TaskListener<Nothing>){
        objectRepo.add(storageModel, objectModel, listener)

    }

}