package io.ymsoft.objectfinder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName =  "positions")
data class PositionModel(
    @PrimaryKey(autoGenerate = true)
    var id : Long?,
    @ColumnInfo(name = "img_url")
    var imgUrl: String? = null,
    @ColumnInfo(name = "pos_x")
    var x:Int? = null,  //픽셸 좌표 x
    @ColumnInfo(name = "pos_y")
    var y:Int? = null,  //픽셸 좌표 y
    @ColumnInfo(name = "name")
    var name:String? = null,
    var memo : String? = null
) {
    @ColumnInfo(name = "created_date")
    var createdDate = Date()
    @ColumnInfo(name = "updated_date")
    var updatedDate = Date()
    @ColumnInfo(name = "objects_text")
    var objString : String? = null
    @Ignore
    val objList = arrayListOf<ObjectModel>()

    fun add(obj: ObjectModel) {
        if(objString.isNullOrBlank()){
            objString = obj.objName
        } else objString += (", " + obj.objName)
    }

    fun remove(obj: ObjectModel) {
        if(objString.isNullOrBlank()){
            objString = obj.objName
        } else objString += (", " + obj.objName)
    }


}