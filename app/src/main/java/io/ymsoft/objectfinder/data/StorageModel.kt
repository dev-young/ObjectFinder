package io.ymsoft.objectfinder.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName =  "storage")
data class StorageModel(
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    @ColumnInfo(name = "img_url")
    var imgUrl: String? = null,
    @ColumnInfo(name = "pos_x")
    var x:Float? = null,  //상대 좌표 x
    @ColumnInfo(name = "pos_y")
    var y:Float? = null,  //상대 좌표 y
    @ColumnInfo(name = "name")
    var name:String? = null,
    var memo : String? = null
) : Serializable{
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
            if(objString!!.isNotEmpty()){
                objString = objString?.substring(2)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StorageModel

        if (id != other.id) return false
        if (imgUrl != other.imgUrl) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (name != other.name) return false
        if (createdDate != other.createdDate) return false
        if (updatedDate != other.updatedDate) return false
        if (objString != other.objString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (imgUrl?.hashCode() ?: 0)
        result = 31 * result + (x?.hashCode() ?: 0)
        result = 31 * result + (y?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + createdDate.hashCode()
        result = 31 * result + updatedDate.hashCode()
        result = 31 * result + (objString?.hashCode() ?: 0)
        return result
    }


}