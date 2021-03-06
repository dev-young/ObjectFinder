package io.ymsoft.objectfinder.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import io.ymsoft.objectfinder.data.StorageModel

@Dao
interface StorageDAO {

    @Query("SELECT * from storage ORDER BY created_date ASC")
    fun observeStorageList() : LiveData<List<StorageModel>>

    @Query("SELECT * FROM storage WHERE id = :id")
    fun observeStorage(id:Long) : LiveData<StorageModel>

    @Query("SELECT * FROM storage WHERE id = :id")
    fun getStorage(id:Long) : StorageModel

    @Query("SELECT * from storage WHERE objects_text LIKE '%' || :objectName || '%' ORDER BY created_date ASC")
    fun observeStorageListContain(objectName: String) : LiveData<List<StorageModel>>

    @Query("SELECT * from storage WHERE objects_text LIKE '%' || :objectName || '%' ORDER BY created_date ASC")
    fun getStorageListContain(objectName: String): List<StorageModel>


    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: StorageModel) : Long

    @Update
    fun update(model: StorageModel)

    @Query("UPDATE storage SET objects_text = :objNames WHERE id = :storageId")
    fun update(storageId: Long, objNames: String?)

    @Delete
    fun delete(model: StorageModel) : Int

    @Query("DELETE FROM storage WHERE id = :id")
    fun delete(id: Long) : Int


}