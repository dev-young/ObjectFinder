/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ymsoft.objectfinder.data.source

import androidx.lifecycle.LiveData
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel
import java.util.ArrayList

/**
 * Interface to the data layer.
 */
interface StorageModelsRepository {

    val isLoading:LiveData<Boolean>

    fun observeStorageModels(): LiveData<Result<List<StorageModel>>>

    /**현재 메모리에 로드된 List<StorageModel>을 리턴한다.*/
    fun getStorageModelsInMemory(): List<StorageModel>

    suspend fun getStorageModels(query: String): Result<List<StorageModel>>

    fun observeStorageModel(StorageModelId: Long): LiveData<Result<StorageModel>>

    suspend fun saveStorageModel(StorageModel: StorageModel): Long

    suspend fun deleteStorageModel(StorageModelId: Long)

    fun observeObjectModels(StorageModelId: Long): LiveData<Result<List<ObjectModel>>>

    /**@param storageModel [ObjectModel]을 추가할 대상
     * @param objName [ObjectModel]의 이름 */
    suspend fun addObject(storageModel: StorageModel, objName: String)

    suspend fun moveObject(objList: List<ObjectModel>, targetStorageId:Long)

    suspend fun deleteObjectModels(idList: ArrayList<Long>): Int

    suspend fun getObjectNames(storageId: Long): List<String>

    suspend fun update(storageId: Long, objectNames: String)
}
