package io.ymsoft.objectfinder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.ymsoft.objectfinder.models.ObjectModel

@Dao
interface ObjectDAO {

    @Query("SELECT * from objects")
    fun getAllObjects() : LiveData<List<ObjectModel>>

    @Query("SELECT * FROM objects WHERE storageId = :id")
    fun getObjectList(id:Long) : LiveData<List<ObjectModel>>

    @Query("SELECT objName FROM objects WHERE storageId = :storageId")
    fun getObectNames(storageId: Long) : List<String>


    @Insert
    fun insert(model:ObjectModel) : Long  //@return rowId

    @Delete
    fun delete(model:ObjectModel) : Int   //@return 삭제된 행 수

    @Query("delete from objects where id in (:models)")
    fun delete(models:List<Long>) : Int   //@return 삭제된 행 수

}