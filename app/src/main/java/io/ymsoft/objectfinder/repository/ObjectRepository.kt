package io.ymsoft.objectfinder.repository

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.TaskListener
import io.ymsoft.objectfinder.db.AppDatabase
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel

@SuppressLint("CheckResult")
object ObjectRepository {

    private lateinit var db: AppDatabase
    private val objectDao by lazy { db.objectDao() }
    private val positionDAO by lazy { db.positionDao() }

    var selectedPosition: PositionModel? = null
    val isWorking = MutableLiveData<Boolean>()  //백그라운드 작업중인지 여부

    fun setContext(application: Application){
        db = AppDatabase.getInstance(application)
    }

    fun getPositionList(): LiveData<List<PositionModel>> {
        return positionDAO.getAllPositions()
    }

    /**오브젝트모델을 넣을 포지션 ID를 찾고 */
    fun add(pos: PositionModel? = null, obj: ObjectModel, listener: TaskListener<Nothing>) {
        isWorking.postValue(true)
        Observable.just(Pair(pos, obj))
            .subscribeOn(Schedulers.io())
            .subscribe{ pair ->
                var positionId = pair.second.positionId
                if(positionId == null){
                    pair.first?.let {p->
                        positionId = positionDAO.insert(p)
                    }
                }

                pair.second.positionId = positionId
                val result = objectDao.insert(pair.second)
                if(result >= 0) {
                    var p = pair.first
                    if(p == null) p = positionDAO.getPosition(positionId!!)
                    p.add(pair.second)
                    p.id = positionId
                    val r = positionDAO.update(p)
                    Log.i("positionDAO.update", r.toString())
                    listener.onComplete(TaskListener.Task(true))
                } else {
                    // TODO: 오류
                    listener.onComplete(TaskListener.Task(true, "포지션 정보 업데이트 실패"))
                }

                isWorking.postValue(false)
            }
    }

}