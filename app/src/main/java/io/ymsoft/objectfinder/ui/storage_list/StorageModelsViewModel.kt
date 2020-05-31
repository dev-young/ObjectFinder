package io.ymsoft.objectfinder.ui.storage_list

import android.app.Application
import androidx.lifecycle.*
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.common.Event
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import kotlinx.coroutines.launch

class StorageModelsViewModel(application: Application) : AndroidViewModel(application) {

    private val storageModelsRepo = (application as MyApp).storageModelsRepository

    val items : LiveData<List<StorageModel>> = storageModelsRepo.observeStorageModels().distinctUntilChanged().switchMap {
        val result = MutableLiveData<List<StorageModel>>()
        if(it is Result.Success){
            result.value = it.data
        } else {

        }
        result
    }
    val isEmpty: LiveData<Boolean> = items.map { it.isEmpty() }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _showDetailEvent = MutableLiveData<Event<StorageModel>>()
    val showDetailEvent : LiveData<Event<StorageModel>> = _showDetailEvent

    fun deleteStorageModel(id:Long){
        viewModelScope.launch {
            storageModelsRepo.deleteStorageModel(id)
        }
    }

}