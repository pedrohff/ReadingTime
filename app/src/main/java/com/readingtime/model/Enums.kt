package com.readingtime.model

import android.content.Context
import com.readingtime.ApplicationContextProvider
import com.readingtime.R

/**
 * Created by pedro on 26/12/17.
 */
enum class BookTypes(val type:String, val description: String) {
    BOOK("B", "Book"),
    GRAPHIC_NOVEL("G", "Graphic Novel")
}

enum class BookType2(val value: Int) {
    BOOK( R.string.booktype_book),
    GRAPHIC_NOVEL(R.string.booktype_graphicnovel);

    fun getMap(context: Context) : MutableMap<Int, String> {
        var map = mutableMapOf<Int, String>()
        for(type in BookType2.values()) {
            map.put(type.value, context.getString(type.value))
        }
        return map
    }
}

enum class BookCategory(val cat:String, val description:String) {
    SCI_FI("SF","Science-Fiction"),
    ADVENTURE("ADV", "Adventure"),
    FANTASY("FTS", "Fantasy")
}

enum class Preferences(val desc: String) {
    LAST_BOOK("PREF_LAST_BOOK"),
    LAST_MILLIS("PREF_LAST_MILLIS")
}

enum class UserBookStatus(val desc: String) {
    READING("Reading"),
    READ("Read"),
    NEW("New");

    companion object {
        fun getEnum(string: String): UserBookStatus {
            val map = mutableMapOf<String, UserBookStatus>()
            map.put("Reading", READING)
            map.put("Read", READ)
            map.put("New", NEW)
            return map.getValue(string)
        }
    }
}