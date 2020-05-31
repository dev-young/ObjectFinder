package io.ymsoft.objectfinder.ui.search

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val _searchResult = MutableLiveData<List<StorageModel>>()
    val searchResult : LiveData<List<StorageModel>> = _searchResult
    val isEmpty: LiveData<Boolean> = searchResult.map { it.isEmpty() }


    private val _isLoading = MutableLiveData<Boolean>()  //로딩중인지 여부
    val isLoading : LiveData<Boolean> = _isLoading

    init {
        _searchResult.value = repo.getStorageModelsInMemory()
    }



    @SuppressLint("CheckResult")
    fun doQuery(query:String){
        _isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val result = repo.getStorageModels(query)
                if (result is Result.Success){
                    _searchResult.postValue(result.data)
                }
                _isLoading.postValue(false)
            }
        }
    }

}