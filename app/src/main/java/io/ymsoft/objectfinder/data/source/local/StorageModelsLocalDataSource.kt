package io.ymsoft.objectfinder.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.data.source.StorageModelsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class StorageModelsLocalDataSource(context: Context) : StorageModelsDataSource {
    private val db : AppDatabase = AppDatabase.getInstance(context)
    private val storageDao = db.storageDao()
    private val objectDao = db.storageDao()
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO


    override fun observeStorageModels(): LiveData<Result<List<StorageModel>>> {
        return storageDao.observeStorageList().map{
            Result.Success(it)
        }


    }

    override suspend fun getStorageModels(): Result<List<StorageModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getStorageModels(query: String): Result<List<StorageModel>> = withContext(ioDispatcher) {
        val result = storageDao.getStorageListContain(query)
        Result.Success(result)
    }

    override fun observeStorageModel(storageModelId: Long): LiveData<Result<StorageModel>> {
        return storageDao.observeStorage(storageModelId).map{
            Result.Success(it)
        }
    }

    override suspend fun getStorageModel(storageModelId: Long): Result<StorageModel> = withContext(ioDispatcher) {
        val result = storageDao.getStorage(storageModelId)
        Result.Success(result)
    }

    override suspend fun addStorage(model: StorageModel) = withContext(ioDispatcher) {
        storageDao.insert(model)
    }

    override suspend fun updateStorage(model: StorageModel) = withContext(ioDispatcher) {
        storageDao.update(model)
    }

    override suspend fun deleteAllStorageModels() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStorageModel(storageModelId: Long) = withContext(ioDispatcher) {
        storageDao.delete(storageModelId)
        return@withContext
    }

    override suspend fun updateObjectNames(storageId: Long, objectNames: String) = withContext(ioDispatcher) {
        storageDao.update(storageId, objectNames)
    }
}