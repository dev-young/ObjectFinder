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
import io.ymsoft.objectfinder.logE
import io.ymsoft.objectfinder.logI
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.StorageModel

@SuppressLint("CheckResult")
object ObjectRepository {

    private lateinit var db: AppDatabase
    private val objectDao by lazy { db.objectDao() }
    private val storageDAO by lazy { db.storageDao() }
    val selectedStorage = MutableLiveData<StorageModel>()
    val storageList by lazy { storageDAO.getAllStorages() }
    private lateinit var objectList : LiveData<List<ObjectModel>>   //selectedStorage 의 오브젝트 리스트

    private var taskCount = 0   // INSERT, UPDATE, DELETE 진행중인 작업 수
    val isWorking = MutableLiveData<Boolean>()  //taskCount > 0 이면 true 아니면 false

    fun setContext(application: Application){
        db = AppDatabase.getInstance(application)
    }



    fun getStorageList(query: String): List<StorageModel> {
        return storageDAO.getStorageListContain(query)
    }

    /**selectedStorage 에 아이디를 통해 Object 리스트 반환 */
    fun getObjList(): LiveData<List<ObjectModel>> {
        var searchId = -1L
        selectedStorage.value?.id?.let { searchId = it }
        objectList = objectDao.getObjectList(searchId)
        return objectList
    }

    fun getObjList(id:Long): LiveData<List<ObjectModel>> {
        objectList = objectDao.getObjectList(id)
        return objectList
    }

    /**INSERT, DELETE, UPDATE 수행시 호출*/
    private fun startTask(){
        taskCount++
        if(taskCount == 1) isWorking.postValue(true)
    }

    /**INSERT, DELETE, UPDATE 완료시 호출*/
    private fun endTask(){
        taskCount--
        if(taskCount == 0) isWorking.postValue(false)
    }

    /**새로운 StorageModel 추가*/
    fun add(pos: StorageModel, listener: TaskListener<StorageModel>? = null) {
        startTask()
        Log.e("", Thread.currentThread().name + " : add 수행")
        Observable.just(pos)
            .subscribeOn(Schedulers.io())
            .map {
                Log.e("", Thread.currentThread().name + " : map 수행")
                Pair(storageDAO.insert(it), it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ pair ->
                Log.e("", Thread.currentThread().name + " : subscribe 수행")
                if(pair.first >= 0) {
                    pair.second.id = pair.first
                    selectedStorage.postValue(pair.second)
                    listener?.onComplete(TaskListener.Task(pair.second))
                } else {
                    listener?.onComplete(TaskListener.Task(false, "StorageModel 추가 실패"))
                }

                endTask()
            }
    }

    /**1. StorageModel 와 ObjectModel 을 동시에 추가하는 경우
     * 2. ObjectModel 만 추가하는 경우
     * */
    fun add(pos: StorageModel? = null, obj: ObjectModel, listener: TaskListener<Nothing>? = null) {
        startTask()
        Observable.just(Pair(pos, obj))
            .subscribeOn(Schedulers.io())
            .subscribe{ pair ->
                var storageId = pair.second.storageId
                if(storageId == null){
                    pair.first?.let {p->
                        storageId = storageDAO.insert(p)
                    }

                    pair.second.storageId = storageId
                }

                val result = objectDao.insert(pair.second)
                if(result >= 0) {
                    var p = pair.first
                    if(p == null) p = storageDAO.getStorage(storageId!!)
                    p.addObjectName(pair.second.objName)
                    p.id = storageId
                    val r = storageDAO.update(p)
                    Log.i("storageDAO.update", r.toString())
                    listener?.onComplete(TaskListener.Task(true))
                } else {
                    // TODO: 오류처리
                    listener?.onComplete(TaskListener.Task(true, "포지션 정보 업데이트 실패"))
                }

                endTask()
            }
    }



    /**ObjectModel 삭제 후 해당 오브젝트를 가지고있던 StorageModel의 정보도 수정하여 업데이트*/
    fun remove(objectModel: ObjectModel) {
        startTask()
        Observable.just(objectModel)
            .subscribeOn(Schedulers.io())
            .subscribe{ model ->
                val r = objectDao.delete(model)
                if(r == 1){
                    val storage = storageDAO.getStorage(model.storageId!!)
                    objectList.value?.let {
                        storage.setObjString(it, objectModel.objName)
                    }
                    storageDAO.update(storage)
                } else {
                    // TODO: 오류처리
                    logE("삭제 실패 -> $model")
                }

                endTask()
            }
    }

    fun remove(vararg models: ObjectModel) {

    }

    fun removeStorage(storageModel: StorageModel) {
        Observable.just(storageModel)
            .subscribeOn(Schedulers.io())
            .subscribe{ model ->
                val r = storageDAO.delete(model)
                if(r == 1){
                    logI("삭제 완료 -> $model")
                } else {
                    // TODO: 오류처리
                    logE("삭제 실패 -> $model")
                }

                endTask()
            }
    }

}