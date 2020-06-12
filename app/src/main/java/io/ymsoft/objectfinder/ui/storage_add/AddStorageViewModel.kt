package io.ymsoft.objectfinder.ui.storage_add

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ymsoft.objectfinder.MyApp
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditStorageViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MyApp).storageModelsRepository

    private val storageDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    private val _storageModel = MutableLiveData<StorageModel?>()
    val storageModel = _storageModel as LiveData<StorageModel?>

    private var isNewStorage = false

    private val _isSaved = MutableLiveData<StorageModel?>()
    val isSaved: LiveData<StorageModel?> = _isSaved

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    //수정하기 전의 이미지 경로 혹은 기존의 보관함에서 사용중인 이미지를 선택했을때 경로를 저장할 변수
    var imagePath: String? = null

    //현재 로드된 사진의 비트맵
    var bitmap: Bitmap? = null


    fun saveStorageModel(model: StorageModel) {
        viewModelScope.launch {
            val insertId = repo.saveStorageModel(model)
            model.id = insertId
            _isSaved.value = model
        }
    }

    fun saveStorageModel(
        name: String,
        point: Pair<Float, Float>?,
        memo: String
    ) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (imagePath.isNullOrEmpty() && bitmap != null) {
                    val f = FileUtil.createImageFile(storageDir)
                    f?.let {
                        FileUtil.saveBitmapToFile(bitmap!!, f)
                        imagePath = f.absolutePath
                    }
                }

                //유효성 검사
                if (imagePath.isNullOrBlank() && name.isBlank()) {
                    _toastMessage.postValue(R.string.please_input_name_or_photo)
                    return@withContext
                }

                //StorageModel 생성
                var model = _storageModel.value
                if (model == null) {
                    model = StorageModel(
                        imgUrl = imagePath,
                        name = name,
                        x = point?.first,
                        y = point?.second,
                        memo = memo
                    )
                } else {
                    model.imgUrl = imagePath
                    model.name = name
                    model.x = point?.first
                    model.y = point?.second
                    model.memo = memo
                }

                saveStorageModel(model)
            }
        }

    }

    fun initModel(storage: StorageModel?) {
        _storageModel.value = storage
        if (storage == null) {
            isNewStorage = true
        } else {
            imagePath = storage.imgUrl
        }
    }

    /**사용자가 이미지를 reset 할 경우 */
    fun clearImage() {
        imagePath = null
        bitmap = null
    }


}