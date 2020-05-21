package io.ymsoft.objectfinder.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.StorageModel

@Database(entities = [ObjectModel::class, StorageModel::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun objectDao() : ObjectDAO
    abstract fun storageDao() : StorageDAO

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