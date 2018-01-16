package com.readingtime.model.local

import android.arch.persistence.room.TypeConverter
import com.readingtime.model.UserBookStatus

/**
 * Created by pedro on 16/01/18.
 */
class Converters {
    @TypeConverter
    fun fromString(string: String): UserBookStatus {
        return UserBookStatus.valueOf(string)
    }

    @TypeConverter
    fun toString(status: UserBookStatus): String {
        return status.toString()
    }
}