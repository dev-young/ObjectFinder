package io.ymsoft.objectfinder.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel

@Database(entities = [ObjectModel::class, PositionModel::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun objectDao() : ObjectDAO
    abstract fun positionDao() : PositionDAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase = INSTANCE?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "Todo.db").build()
    }
}