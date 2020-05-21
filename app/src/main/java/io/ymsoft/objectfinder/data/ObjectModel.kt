package io.ymsoft.objectfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.ymsoft.objectfinder.util.CheckableChipGroupHelper
import java.util.*

@Entity(tableName =  "objects")
data class ObjectModel (
    @PrimaryKey(autoGenerate = true)
    val id : Long? = null,
    var storageId: Long? = null,
    var objName: String,
    var memo : String? = null
) : CheckableChipGroupHelper.ChipModel{
    override val modelId: Long
        get() = id!!
    override val modelName: String
        get() = objName

    var date = Date()



}