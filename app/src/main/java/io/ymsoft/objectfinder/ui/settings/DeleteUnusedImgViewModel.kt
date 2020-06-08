package io.ymsoft.objectfinder.ui.settings

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DeleteUnusedImgViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as MyApp).storageModelsRepository

    private val _isWorking = MutableLiveData(false)
    val isWorking = _isWorking as LiveData<Boolean>

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage = _toastMessage as LiveData<Int>

    private val _unusedFiles = MutableLiveData<List<String>>()
    val unusedFiles : LiveData<List<String>> = _unusedFiles

    private val _deleteAllCompleteEvent = MediatorLiveData<Boolean>()
    val deleteAllCompleteEvent : MediatorLiveData<Boolean> = _deleteAllCompleteEvent


    fun loadUnusedImage(directory: File?) {
        viewModelScope.launch {
            _isWorking.value = true
            withContext(Dispatchers.IO){
                val set = repo.getStorageModelsInMemory().let {
                    val set = hashSetOf<String>().apply {
                        it.forEach {
                            it.imgUrl?.let { add(it) }
                        }
                    }
                    set
                }

                val unusedFile = hashSetOf<String>().apply {
                    directory?.listFiles()?.forEach {
                        if(it.length() > 0)
                            add(it.absolutePath)
                    }
                }.also {totalPath ->
                    set.forEach {
                        totalPath.remove(it)
                    }
                }
                _unusedFiles.postValue(unusedFile.toList())
            }
            _isWorking.value = false
        }
    }

    private fun showToast(@StringRes msg : Int){
        _toastMessage.postValue(msg)
    }

    fun removeImages(filePathList: List<String>) {
        viewModelScope.launch {
            _isWorking.value = true
            var isDeleteAll = false
            withContext(Dispatchers.IO){
                filePathList.forEach {
                    FileUtil.delete(it)
                }
                val set = _unusedFiles.value?.toHashSet()
                set?.let {
                    it.removeAll(filePathList)
                    isDeleteAll = it.isEmpty()
                    if(!isDeleteAll)
                        _unusedFiles.postValue(it.toList())
                }


            }
            showToast(R.string.message_delete_complete)
            _isWorking.value = false
            if(isDeleteAll) {
                _deleteAllCompleteEvent.value = true
            }

        }
    }

}