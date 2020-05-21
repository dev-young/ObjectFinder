package io.ymsoft.objectfinder.data.source.local

import androidx.room.TypeConverter
import java.util.*

class TypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toDate(timestamp: Long): Date {
            return Date(timestamp)
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Date): Long {
            return date.time
        }
    }

}