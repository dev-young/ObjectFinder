package io.ymsoft.objectfinder.models

import androidx.room.ColumnInfo
import androidx.room.Entity
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
//    @Ignore
//    val objList = arrayListOf<ObjectModel>()


    /**objString에 새롭게 추가할 물건의 이름을 추가한다.*/
    fun addObjectName(name: String) {
        if(objString.isNullOrBlank()){
            objString = name
        } else objString += (", $name")
    }


    /**list의 내용들을 바탕으로 objString 작성
     * @param excludeName 리시트에서 제외시킬 이름 (여러개가 있을경우 하나만 제외된다)*/
    fun setObjString(list: List<ObjectModel>, excludeName:String) {
        var exCount = 0
        objString = ""
        if(list.isNotEmpty()){
            list.forEach {
                if(exCount == 0 && excludeName == it.objName){
                    exCount++
                } else {
                    objString += (", " + it.objName)
                }
            }
            objString?.isNotEmpty().let {
                objString = objString?.substring(2)
            }
        }
    }


}