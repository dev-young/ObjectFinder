package io.ymsoft.objectfinder.data.source

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.data.source.local.ObjectModelsLocalDataSource
import io.ymsoft.objectfinder.data.source.local.StorageModelsLocalDataSource
import io.ymsoft.objectfinder.util.logE
import io.ymsoft.objectfinder.util.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.*

class DefaultStorageModelsRepo(context: Context) : StorageModelsRepository {

    private val storageDataSource: StorageModelsDataSource
    private val objectDataSource: ObjectModelsDataSource

    private lateinit var loadedStorageModels : LiveData<Result<List<StorageModel>>>

    private val _isLoading = MutableLiveData(false)
    override val isLoading: LiveData<Boolean> = _isLoading

    init {
        storageDataSource = StorageModelsLocalDataSource(context)
        objectDataSource = ObjectModelsLocalDataSource(context)
    }

    override fun observeStorageModels(): LiveData<Result<List<StorageModel>>> {
        loadedStorageModels = storageDataSource.observeStorageModels()
        return loadedStorageModels
    }

    override fun getStorageModelsInMemory(): List<StorageModel> {
        val result = loadedStorageModels.value
        return if(result is Result.Success){
            result.data
        } else
            emptyList()
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

    override suspend fun moveObject(objList: List<ObjectModel>, targetStorageId: Long) {
        withContext(Dispatchers.Default){
            val currentStorageId = objList[0].storageId!!
            val idList = arrayListOf<Long>().apply { objList.forEach { it.id?.let { add(it) } } }
            objectDataSource.moveObjectModels(idList, targetStorageId)
            val currentStorageNames = objectDataSource.getObjectNames(currentStorageId).joinToString()
            val targetStorageNames = objectDataSource.getObjectNames(targetStorageId).joinToString()
            storageDataSource.updateObjectNames(currentStorageId, currentStorageNames)
            storageDataSource.updateObjectNames(targetStorageId, targetStorageNames)
        }
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