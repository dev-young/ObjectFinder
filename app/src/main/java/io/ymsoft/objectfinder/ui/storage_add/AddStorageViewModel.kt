package io.ymsoft.objectfinder.ui.storage_add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.util.makeToast
import kotlinx.coroutines.launch

class AddEditStorageViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val _storageModel = MutableLiveData<StorageModel?>()
    val storageModel = _storageModel as LiveData<StorageModel?>

    private var isNewStorage = false

    private val _isSaved = MutableLiveData<StorageModel?>()
    val isSaved : LiveData<StorageModel?> = _isSaved

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage : LiveData<Int> = _toastMessage


    fun saveStorageModel(model: StorageModel) {
        viewModelScope.launch {
            val insertId = repo.saveStorageModel(model)
            model.id = insertId
            _isSaved.value = model
        }
    }

    fun saveStorageModel(
        photoUrl: String?,
        name: String,
        point: Pair<Float, Float>?,
        memo: String
    ) {
        //유효성 검사
        if(photoUrl.isNullOrBlank() && name.isBlank()){
            _toastMessage.postValue(R.string.please_input_name_or_photo)
            return
        }

        //StorageModel 생성
        var model = _storageModel.value
        if(model == null){
            model = StorageModel(
                imgUrl = photoUrl,
                name = name,
                x = point?.first,
                y = point?.second,
                memo = memo
            )
        } else {
            model.imgUrl = photoUrl
            model.name = name
            model.x = point?.first
            model.y = point?.second
            model.memo = memo
        }

        saveStorageModel(model)

    }

    fun initModel(storage: StorageModel?) {
        _storageModel.value = storage
        if(storage == null){
            isNewStorage = true
        }
    }


}