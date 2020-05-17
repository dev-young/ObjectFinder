package io.ymsoft.objectfinder.repository

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
    val selectedPosition = MutableLiveData<PositionModel>()
    private lateinit var objectList : LiveData<List<ObjectModel>>   //selectedPosition 의 오브젝트 리스트

    private var taskCount = 0   // INSERT, UPDATE, DELETE 진행중인 작업 수
    val isWorking = MutableLiveData<Boolean>()  //백그라운드 작업중인지 여부

    fun setContext(application: Application){
        db = AppDatabase.getInstance(application)
    }

    fun getPositionList(): LiveData<List<PositionModel>> {
        return positionDAO.getAllPositions()
    }

    /**selectedPosition 에 아이디를 통해 Object 리스트 반환 */
    fun getObjList(): LiveData<List<ObjectModel>> {
        var searchId = -1L
        selectedPosition.value?.id?.let { searchId = it }
        objectList = objectDao.getObjectList(searchId)
        return objectList
    }

    fun getObjList(id:Long): LiveData<List<ObjectModel>> {
        objectList = objectDao.getObjectList(id)
        return objectList
    }

    /**새로운 PositionModel 추가*/
    fun add(pos: PositionModel, listener: TaskListener<PositionModel>? = null) {
        startTask()
        Log.e("", Thread.currentThread().name + " : add 수행");
        Observable.just(pos)
            .subscribeOn(Schedulers.io())
            .map {
                Log.e("", Thread.currentThread().name + " : map 수행");
                Pair(positionDAO.insert(it), it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ pair ->
                Log.e("", Thread.currentThread().name + " : subscribe 수행");
                if(pair.first >= 0) {
                    pair.second.id = pair.first
                    selectedPosition.postValue(pair.second)
                    listener?.onComplete(TaskListener.Task(pair.second))
                } else {
                    listener?.onComplete(TaskListener.Task(false, "PositionModel 추가 실패"))
                }

                endTask()
            }
    }

    /**1. PositionModel 와 ObjectModel 을 동시에 추가하는 경우
     * 2. ObjectModel 만 추가하는 경우
     * */
    fun add(pos: PositionModel? = null, obj: ObjectModel, listener: TaskListener<Nothing>? = null) {
        startTask()
        Observable.just(Pair(pos, obj))
            .subscribeOn(Schedulers.io())
            .subscribe{ pair ->
                var positionId = pair.second.positionId
                if(positionId == null){
                    pair.first?.let {p->
                        positionId = positionDAO.insert(p)
                    }

                    pair.second.positionId = positionId
                }

                val result = objectDao.insert(pair.second)
                if(result >= 0) {
                    var p = pair.first
                    if(p == null) p = positionDAO.getPosition(positionId!!)
                    p.addObjectName(pair.second.objName)
                    p.id = positionId
                    val r = positionDAO.update(p)
                    Log.i("positionDAO.update", r.toString())
                    listener?.onComplete(TaskListener.Task(true))
                } else {
                    // TODO: 오류처리
                    listener?.onComplete(TaskListener.Task(true, "포지션 정보 업데이트 실패"))
                }

                endTask()
            }
    }

    private fun startTask(){
        taskCount++
        if(taskCount == 1) isWorking.postValue(true)
    }
    private fun endTask(){
        taskCount--
        if(taskCount == 0) isWorking.postValue(false)
    }

    fun remove(objectModel: ObjectModel) {
        startTask()
        Observable.just(objectModel)
            .subscribeOn(Schedulers.io())
            .subscribe{ model ->
                val r = objectDao.delete(model)
                if(r == 1){
                    val position = positionDAO.getPosition(model.positionId!!)
                    objectList.value?.let {
                        position.setObjString(it, objectModel.objName)
                    }
                    positionDAO.update(position)
                } else {
                    // TODO: 오류처리
                }

                endTask()
            }
    }

    fun remove(vararg models: ObjectModel) {

    }

}