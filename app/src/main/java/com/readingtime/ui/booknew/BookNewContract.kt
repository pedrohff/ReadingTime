package com.readingtime.ui.booknew

import android.content.Context
import android.content.Intent
import com.readingtime.model.Book

/**
 * Created by pedro on 08/01/18.
 */

interface BookNewContract {

    interface View {
        fun updateImageTextviews(string: String)
        fun showProgressBar()
        fun hideProgressBar()
        fun makeToast(msg: String)
        fun setBookCoverUrl(url: String)
    }

    interface Presenter {
        fun subscribe()
        fun unsubscribe()
        fun saveBook(book: Book)
        fun updateSelectedImage(requestCode: Int, resultCode: Int, data: Intent?, context: Context)
    }
}