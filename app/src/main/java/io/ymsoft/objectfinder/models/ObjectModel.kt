package io.ymsoft.objectfinder.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.ymsoft.objectfinder.utils.CheckableChipGroupHelper
import java.util.*

@Entity(tableName =  "objects")
data class ObjectModel (
    @PrimaryKey(autoGenerate = true)
    val id : Long? = null,
    var storageId: Long? = null,
    var objName: String,
    var memo : String? = null
) : CheckableChipGroupHelper.ChipModel{
    override fun getModelId(): Long {
        return id!!
    }

    override fun getModelName(): String {
        return objName
    }

    var date = Date()



}