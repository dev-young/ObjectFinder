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
import io.ymsoft.objectfinder.data.ObjectModel

/**
 * Main entry point for accessing ObjectModels data.
 */
interface ObjectModelsDataSource {

    fun observeObjectModels(storageId : Long): LiveData<Result<List<ObjectModel>>>

    suspend fun getObjectModels(storageId : Long): Result<List<ObjectModel>>

    suspend fun getObjectModel(id: Long): Result<ObjectModel>

    suspend fun addObjectModel(objectModel: ObjectModel) : Long

    suspend fun moveObjectModels(idList: List<Long>, targetStorageId: Long)

    suspend fun deleteObjectModels(idList: List<Long>): Int

    suspend fun deleteObjectModel(model: ObjectModel)

    suspend fun getObjectNames(storageId: Long): List<String>
}
