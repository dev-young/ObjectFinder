package io.ymsoft.objectfinder.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.ymsoft.objectfinder.models.StorageModel

@Dao
interface StorageDAO {

    @Query("SELECT * from storage ORDER BY created_date ASC")
    fun getAllStorages() : LiveData<List<StorageModel>>

    @Query("SELECT * FROM storage WHERE id = :id")
    fun getStorage(id:Long) : StorageModel

    @Query("SELECT * from storage WHERE objects_text LIKE '%' || :objectName || '%' ORDER BY created_date ASC")
    fun getStorageListContain(objectName: String): List<StorageModel>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(model:StorageModel) : Long

    @Update
    fun update(model:StorageModel)

    @Delete
    fun delete(model:StorageModel) : Int

}