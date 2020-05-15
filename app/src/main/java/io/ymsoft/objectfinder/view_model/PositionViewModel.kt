package io.ymsoft.objectfinder.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.repository.ObjectRepository

class PositionViewModel(application: Application) : AndroidViewModel(application) {

    private val objectRepo = ObjectRepository
    val positionList = objectRepo.getPositionList()
    val isWorking = objectRepo.isWorking  //백그라운드 작업중인지 여부

    fun getSelectedPosition(): PositionModel? {
        return objectRepo.selectedPosition
    }

    fun setSelectedPosition(positionModel: PositionModel){
        objectRepo.selectedPosition = positionModel
    }


    fun addNew(positionModel:PositionModel? = null, objectModel: ObjectModel, listener: TaskListener<Nothing>){
        objectRepo.add(positionModel, objectModel, listener)

    }

}