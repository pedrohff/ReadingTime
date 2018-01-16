package com.readingtime.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by pedro on 15/01/18.
 */
@Entity
class UserFlag(
        @PrimaryKey
        var id: String,
        var booksToUpdate: MutableList<String>? = null,
        var userBooksToUpdate: MutableList<String>? = null,
        var recordsToUpdate: MutableList<String>? = null,
        var lastBookUpdate: String? = null,
        var lastUserBookUpdate: String? = null,
        var lastRecordUpdate: String? = null
) {
    enum class Flag {
        REMOTE,
        LOCAL;
    }
}