package io.ymsoft.objectfinder.ui.storage_add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.data.StorageModel
import kotlinx.coroutines.launch

class AddEditStorageViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val _isSaved = MutableLiveData<StorageModel?>()
    val isSaved : LiveData<StorageModel?> = _isSaved




    fun saveStorageModel(model: StorageModel) {
        viewModelScope.launch {
            val insertId = repo.saveStorageModel(model)
            model.id = insertId
            _isSaved.value = model
        }
    }


}