package io.ymsoft.objectfinder.ui.detail

import android.app.Application
import androidx.lifecycle.*
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.util.logE
import io.ymsoft.objectfinder.util.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.ArrayList

class StorageDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val _storageId = MutableLiveData<Long>()
    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

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
        _isLoading.postValue(true)
        repo.observeObjectModels(it).map { result ->
            _isLoading.postValue(false)
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

    fun moveStorage(objList: List<ObjectModel>, targetStorageId: Long){
        viewModelScope.launch {
            _isLoading.value = true
            repo.moveObject(objList, targetStorageId)
            _isLoading.postValue(false)
            _isLoading.value = false
        }
    }

    fun removeStorage() = viewModelScope.launch {
        val storageId = _storageId.value
        storageId?.let {
            _isLoading.postValue(true)
            repo.deleteStorageModel(it)
            _isLoading.postValue(false)
        }
    }

    fun getCurrentStorageList(): List<StorageModel> {
        return repo.getStorageModelsInMemory()
    }

}