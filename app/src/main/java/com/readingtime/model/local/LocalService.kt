package com.readingtime.model.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.readingtime.model.Book
import com.readingtime.model.Record
import com.readingtime.model.UserBook

/**
 * Created by pedro on 15/01/18.
 */
@Database(entities = arrayOf(UserBook::class, Book::class, Record::class), version = 1)
@TypeConverters(Converters::class)
abstract class LocalService : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun recordDao(): RecordDao
    abstract fun userBookDao(): UserBookDao
    abstract fun userFlagDao(): UserFlagDao
}
