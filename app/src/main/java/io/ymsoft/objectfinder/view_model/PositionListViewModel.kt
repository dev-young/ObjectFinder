package io.ymsoft.objectfinder.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PositionListViewModel(application: Application) : AndroidViewModel(application) {

    val positionList = MutableLiveData<List<String>>().apply {
        value = arrayListOf("test1", "test2")
    }


}