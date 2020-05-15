package io.ymsoft.objectfinder.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.ymsoft.objectfinder.models.PositionModel

@Dao
interface PositionDAO {

    @Query("SELECT * from positions ORDER BY created_date ASC")
    fun getAllPositions() : LiveData<List<PositionModel>>

    @Query("SELECT * FROM positions WHERE id = :id")
    fun getPosition(id:Long) : PositionModel

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj:PositionModel) : Long

    @Update
    fun update(obj:PositionModel)

    @Delete
    fun delete(obj:PositionModel)
}