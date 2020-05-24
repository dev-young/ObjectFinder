package io.ymsoft.objectfinder.data.source

import android.content.Context
import androidx.lifecycle.LiveData
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.data.source.local.ObjectModelsLocalDataSource
import io.ymsoft.objectfinder.data.source.local.StorageModelsLocalDataSource
import kotlinx.coroutines.coroutineScope
import java.util.*

class DefaultStorageModelsRepo(context: Context) : StorageModelsRepository {

    private val storageDataSource: StorageModelsDataSource
    private val objectDataSource: ObjectModelsDataSource

    init {
        storageDataSource = StorageModelsLocalDataSource(context)
        objectDataSource = ObjectModelsLocalDataSource(context)
    }

    override fun observeStorageModels(): LiveData<Result<List<StorageModel>>> {
        return storageDataSource.observeStorageModels()
    }

    override suspend fun getStorageModels(query: String): Result<List<StorageModel>> {
        return storageDataSource.getStorageModels(query)
    }

    override fun observeStorageModel(StorageModelId: Long): LiveData<Result<StorageModel>> {
        return storageDataSource.observeStorageModel(StorageModelId)
    }

    override suspend fun saveStorageModel(model: StorageModel): Long {
        var id = model.id
        if(id == null){
            id = storageDataSource.addStorage(model)
        } else {
            storageDataSource.updateStorage(model)
        }
        return id
    }

    override suspend fun deleteStorageModel(StorageModelId: Long) {
        storageDataSource.deleteStorageModel(StorageModelId)
    }

    override fun observeObjectModels(StorageModelId: Long): LiveData<Result<List<ObjectModel>>> {
        return objectDataSource.observeObjectModels(StorageModelId)
    }

    override suspend fun addObject(storageModel: StorageModel, objName: String) = coroutineScope {
        val model = ObjectModel(storageId = storageModel.id, objName = objName)
        val result = objectDataSource.addObjectModel(model)
        if(result > 0) {
            storageModel.addObjectName(objName)
            storageDataSource.addStorage(storageModel)
        }
        return@coroutineScope
    }

    override suspend fun deleteObjectModels(idList: ArrayList<Long>) = coroutineScope {
        objectDataSource.deleteObjectModels(idList)
    }

    override suspend fun getObjectNames(storageId: Long): List<String> {
        return objectDataSource.getObjectNames(storageId)
    }

    override suspend fun update(storageId: Long, objectNames: String) {
        storageDataSource.updateObjectNames(storageId, objectNames)
    }


}