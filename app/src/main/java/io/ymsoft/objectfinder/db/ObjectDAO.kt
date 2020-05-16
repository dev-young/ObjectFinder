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

    @Query("SELECT * FROM objects WHERE positionId = :id")
    fun getObjectList(id:Long) : LiveData<List<ObjectModel>>

    @Insert
    fun insert(model:ObjectModel) : Long  //@return rowId

    @Delete
    fun delete(model:ObjectModel) : Int   //@return 삭제된 행 수
}