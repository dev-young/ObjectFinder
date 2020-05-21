package io.ymsoft.objectfinder.ui.search

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.data.source.ObjectRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val objectRepo = ObjectRepository
    private val searchResult = MutableLiveData<List<StorageModel>>()

    val isLoading = MutableLiveData<Boolean>()  //로딩중인지 여부

    init {
        searchResult.value = objectRepo.storageList.value
    }

    fun getSearchResult(): MutableLiveData<List<StorageModel>> {
        return searchResult
    }

    @SuppressLint("CheckResult")
    fun doQuery(query:String){
        isLoading.value = true
        Observable.just(query)
            .subscribeOn(Schedulers.io())
            .subscribe {
                val list = objectRepo.getStorageList(query)
                searchResult.postValue(list)
                isLoading.postValue(false)
            }
    }

    fun select(model: StorageModel?) {
        model?.let {

        }
    }

}