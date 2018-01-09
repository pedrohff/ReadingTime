package com.readingtime.ui.booknew

import android.content.ContentResolver
import android.content.Intent
import com.readingtime.model.Book

/**
 * Created by pedro on 08/01/18.
 */

interface BookNewContract {

    interface View {
        fun updateImageTextviews(byteCount: Int)
        fun showProgressBar()
        fun hideProgressBar()
        fun makeToast(msg: String)
        fun setBookCoverUrl(url: String)
    }

    interface Presenter {
        fun subscribe()
        fun unsubscribe()
        fun saveBook(book: Book)
        fun updateSelectedImage(requestCode: Int, resultCode: Int, data: Intent?, contentResolver: ContentResolver)
    }
}