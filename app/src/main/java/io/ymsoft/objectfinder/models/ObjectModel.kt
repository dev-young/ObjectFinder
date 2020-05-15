package io.ymsoft.objectfinder.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName =  "objects")
data class ObjectModel(
    @PrimaryKey(autoGenerate = true)
    val id : Long?,
    var positionId: Long?,
    var objName: String,
    var memo : String? = null
) {

    var date = Date()



}