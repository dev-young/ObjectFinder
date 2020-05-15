package io.ymsoft.objectfinder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.ymsoft.objectfinder.models.ObjectModel

@Dao
interface ObjectDAO {

    @Query("SELECT * from objects")
    fun getAllObjects() : LiveData<List<ObjectModel>>

    @Insert
    fun insert(obj:ObjectModel) : Long
}