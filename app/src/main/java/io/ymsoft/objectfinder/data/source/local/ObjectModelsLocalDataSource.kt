package io.ymsoft.objectfinder.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.source.ObjectModelsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ObjectModelsLocalDataSource(context: Context) : ObjectModelsDataSource {
    private val db : AppDatabase = AppDatabase.getInstance(context)
    private val storageDao = db.storageDao()
    private val objectDao = db.objectDao()
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO


    override fun observeObjectModels(storageId : Long): LiveData<Result<List<ObjectModel>>> {
        return objectDao.getObjectList(storageId).map{
            Result.Success(it)
        }
    }

    override suspend fun getObjectModels(storageId: Long): Result<List<ObjectModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getObjectModel(id: Long): Result<ObjectModel> {
        TODO("Not yet implemented")
    }

    override suspend fun addObjectModel(ObjectModel: ObjectModel) : Long = withContext(ioDispatcher) {
        val r = objectDao.insert(ObjectModel)
        if(r > 0){
            Timber.i("${Thread.currentThread().name} -> ${ObjectModel.objName} 추가 완료")
        } else {
            Timber.e("${Thread.currentThread().name} -> ${ObjectModel.objName} 추가 실패")
        }
        r
    }

    override suspend fun deleteObjectModels(idList: List<Long>) = withContext(ioDispatcher) {
        objectDao.delete(idList)
    }

    override suspend fun deleteObjectModel(model: ObjectModel) = withContext(ioDispatcher) {
        objectDao.delete(model)
        return@withContext
    }

    override suspend fun getObjectNames(storageId: Long): List<String>  = withContext(ioDispatcher) {
        objectDao.getObjectNames(storageId)
    }


}