package io.ymsoft.objectfinder.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.repository.ObjectRepository

class PositionViewModel(application: Application) : AndroidViewModel(application) {

    private val objectRepo = ObjectRepository
    val positionList = objectRepo.getPositionList()
    val isWorking = objectRepo.isWorking  //백그라운드 작업중인지 여부
    val toastMsg = MutableLiveData<@androidx.annotation.StringRes Int>()
    fun getSelectedPosition(): MutableLiveData<PositionModel> {
        return objectRepo.selectedPosition
    }

    fun setSelectedPosition(positionModel: PositionModel){
        objectRepo.selectedPosition.postValue(positionModel)
    }

    fun getObjectList(): LiveData<List<ObjectModel>> {
        return objectRepo.getObjList()
    }


    fun addNew(positionModel:PositionModel? = null, objectModel: ObjectModel, listener: TaskListener<Nothing>){
        objectRepo.add(positionModel, objectModel, listener)
        toastMsg.postValue(R.string.add)
    }

    /**selectedPosition에 새로운 오브젝트를 추가한다.*/
    fun addNewObject(name: String) {
        objectRepo.selectedPosition.value?.id?.let {id ->
            val obj = ObjectModel(positionId = id, objName = name)
            objectRepo.add(obj = obj)
        }
        toastMsg.postValue(R.string.add)
    }

    fun onObjectLongClicked(model: ObjectModel) {
        //삭제
        objectRepo.remove(model)
    }

    fun itemlongClicked(model: PositionModel) {
        objectRepo.removePosition(model)
    }

}