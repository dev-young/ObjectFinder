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
import io.ymsoft.objectfinder.data.Result
import io.ymsoft.objectfinder.data.StorageModel

/**
 * Main entry point for accessing StorageModels data.
 */
interface StorageModelsDataSource {

    fun observeStorageModels(): LiveData<Result<List<StorageModel>>>

    suspend fun getStorageModels(): Result<List<StorageModel>>

    fun observeStorageModels(query: String): LiveData<Result<List<StorageModel>>>

    suspend fun getStorageModels(query: String) : Result<List<StorageModel>>

    fun observeStorageModel(storageModelId: Long): LiveData<Result<StorageModel>>

    suspend fun getStorageModel(storageModelId: Long): Result<StorageModel>

    suspend fun addStorage(model: StorageModel): Long

    suspend fun updateStorage(model: StorageModel)

    suspend fun deleteAllStorageModels()

    suspend fun deleteStorageModel(storageModelId: Long)

    suspend fun updateObjectNames(storageId: Long, objectNames: String)
}
