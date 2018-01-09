package com.readingtime.ui.recording

import android.content.Context
import android.widget.ImageView
import com.readingtime.model.UserBook
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */
interface RecordContract {

    interface View

    interface Presenter {
        fun loadBookCover(url: String, view: ImageView, context: Context)
        fun saveRecord(currentDate: Date, time: Long, pagenum: Int, uBook: UserBook)
        fun updateUserBook(uBook: UserBook, pagenum: Int, time: Long)
        fun subscribe(bookId: String)
        fun unsubscribe()
    }
}