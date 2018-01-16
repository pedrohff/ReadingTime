package com.readingtime.model.local

import android.arch.persistence.room.*
import com.readingtime.model.UserBook
import io.reactivex.Single

/**
 * Created by pedro on 15/01/18.
 */
@Dao
interface UserBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userBook: UserBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userBooks: List<UserBook>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(userBook: UserBook)

    @Query("SELECT * FROM UserBook ORDER BY lastVisit DESC LIMIT 10")
    fun last(): Single<List<UserBook>>
}