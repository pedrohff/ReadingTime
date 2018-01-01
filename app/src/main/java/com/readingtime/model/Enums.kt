package com.readingtime.model

/**
 * Created by pedro on 26/12/17.
 */
enum class BookTypes(val type:String, val description: String) {
    BOOK("B", "Book"),
    GRAPHIC_NOVEL("G", "Graphic Novel")
}

enum class BookCategory(val cat:String, val description:String) {
    SCI_FI("SF","Science-Fiction"),
    ADVENTURE("ADV", "Adventure"),
    FANTASY("FTS", "Fantasy")
}

enum class Preferences(val desc: String) {
    LAST_BOOK("PREF_LAST_BOOK")
}