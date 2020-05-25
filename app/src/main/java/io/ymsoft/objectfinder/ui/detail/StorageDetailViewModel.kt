package io.ymsoft.objectfinder.ui.detail

import android.app.Application
import androidx.lifecycle.*
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class StorageDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val _storageId = MutableLiveData<Long>()
    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage : LiveData<Int> = _toastMessage

    val storageModel = _storageId.switchMap { id ->
        repo.observeStorageModel(id).map { result ->
            if(result is Result.Success){
                return@map result.data
            } else {
                // TODO: 에러 처리
                null
            }
        }
    }

    val objectModels = _storageId.switchMap {
        repo.observeObjectModels(it).map { result ->
            if(result is Result.Success){
                return@map result.data
            } else{
                // TODO: 에러 처리
                null
            }
        }
    }

    fun startObserve(storageId : Long) {
        Timber.i("startObserve storageId: $storageId")
       _storageId.value = storageId
    }



    fun deleteObjects(checkedList: List<ObjectModel>) = viewModelScope.launch {
//        objectRepo.removeObjects(checkedList)
        withContext(Dispatchers.Default) {
            val idList = arrayListOf<Long>().apply {
                checkedList.forEach {
                    it.id?.let { it -> add(it) }
                }
            }
            repo.deleteObjectModels(idList)
            _storageId.value?.let {
                val nameList = repo.getObjectNames(it)
                repo.update(it, nameList.joinToString())
            }
        }

    }

    fun addNewObject(objName: String) = viewModelScope.launch {
        val storage = storageModel.value
        storage?.let {
            repo.addObject(it, objName)
        }
    }

    fun removeStorage() = viewModelScope.launch {
        val storageId = _storageId.value
        storageId?.let {
            repo.deleteStorageModel(it)
        }
    }

}