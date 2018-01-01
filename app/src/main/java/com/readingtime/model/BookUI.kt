package com.readingtime.model

import com.readingtime.extensions.millisToString

/**
 * Created by pedro on 01/01/18.
 */
data class BookUI(var book: Book?,
                  var lastRecord: Record?,
                  var timeRead: String,
                  var percentage: String) {
    companion object {
        fun construct(book: Book?, records: List<Record>?): BookUI {
            val lastRecord = records?.last()
            var percentage = "0%"
            if (book != null && lastRecord != null) {
                percentage = String.format("%d%%", lastRecord.pageStopped * 100 / book.pages)
            }
            val millis: Long? = records?.fold(0, { acc: Long, record -> acc + record.milisRead })
            val timeString = millisToString(millis)
            return BookUI(book, records?.last(), timeString, percentage)
        }
    }
}