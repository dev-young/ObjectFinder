package io.ymsoft.objectfinder.ui.storage_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ymsoft.objectfinder.common.TaskListener
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.data.source.ObjectRepository

class StorageListViewModel(application: Application) : AndroidViewModel(application) {

    private val objectRepo = ObjectRepository
    val storageList = objectRepo.storageList
    val isWorking = objectRepo.isWorking  //백그라운드 작업중인지 여부
    val toastMsg = MutableLiveData<@androidx.annotation.StringRes Int>()
    fun getSelectedStorage(): MutableLiveData<StorageModel> {
        return objectRepo.selectedStorage
    }

    fun setSelectedStorage(storageModel: StorageModel){
        objectRepo.selectedStorage.postValue(storageModel)
    }

    fun getObjectList(): LiveData<List<ObjectModel>> {
        return objectRepo.getObjList()
    }


    fun addNew(storageModel: StorageModel? = null, objectModel: ObjectModel, listener: TaskListener<Nothing>){
        objectRepo.add(storageModel, objectModel, listener)
//        toastMsg.postValue(R.string.add)
    }

    /**selectedStorage에 새로운 오브젝트를 추가한다.*/
    fun addNewObject(name: String) {
        objectRepo.selectedStorage.value?.id?.let {id ->
            val obj = ObjectModel(
                storageId = id,
                objName = name
            )
            objectRepo.add(obj = obj)
        }
//        toastMsg.postValue(R.string.add)
    }

    fun onObjectLongClicked(model: ObjectModel) {
        //삭제
        objectRepo.remove(model)
    }

    fun itemlongClicked(model: StorageModel) {
        objectRepo.removeStorage(model)
    }

    fun delete(checkedList: List<ObjectModel>) {
        objectRepo.removeObjects(checkedList)
    }

}