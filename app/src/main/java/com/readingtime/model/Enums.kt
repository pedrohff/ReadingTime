package com.readingtime.model

import android.content.Context
import com.readingtime.R


/**
 * Created by pedro on 26/12/17.
 */
enum class BookType(val value: Int) {
    BOOK(R.string.booktype_book),
    GRAPHIC_NOVEL(R.string.booktype_graphicnovel),
    MANGA(R.string.booktype_manga);

    companion object {
        fun getMap(context: Context): MutableMap<Int, String> {
            var map = mutableMapOf<Int, String>()
            for (type in BookType.values()) {
                map.put(type.value, context.getString(type.value))
            }
            return map
        }

        fun getEnumFromValue(map: MutableMap<Int, String>, value: String): BookType? {
            val key = BookType.getKeyFromValue(map, value)
            return BookType.helperMap()[key]
        }

        private fun helperMap(): MutableMap<Int, BookType> {
            val mapAux = mutableMapOf<Int, BookType>()
            for (cat: BookType in BookType.values()) {
                mapAux.put(cat.value, cat)
            }
            return mapAux
        }

        private fun getKeyFromValue(map: MutableMap<Int, String>, value: String): Int? {
            for (key: Int in map.keys) {
                if (map[key].equals(value)) {
                    return key
                }
            }
            return null
        }
    }
}

enum class BookCategory(val value: Int) {
    ADVENTURE(R.string.bookcategory_adventure),
    FANTASY(R.string.bookcategory_fantasy),
    SCI_FI(R.string.bookcategory_scify);


    companion object {

        private fun helperMap(): MutableMap<Int, BookCategory> {
            val mapAux = mutableMapOf<Int, BookCategory>()
            for (cat: BookCategory in BookCategory.values()) {
                mapAux.put(cat.value, cat)
            }
            return mapAux
        }


        fun getMap(context: Context): MutableMap<Int, String> {
            var map = mutableMapOf<Int, String>()
            for (type in BookCategory.values()) {
                map.put(type.value, context.getString(type.value))
            }
            return map
        }

        fun getEnumFromValue(map: MutableMap<Int, String>, value: String): BookCategory? {
            val key = getKeyFromValue(map, value)
            return helperMap()[key]
        }

        private fun getKeyFromValue(map: MutableMap<Int, String>, value: String): Int? {
            for (key: Int in map.keys) {
                if (map[key].equals(value)) {
                    return key
                }
            }
            return null
        }
    }
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