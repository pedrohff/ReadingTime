package com.readingtime.ui.main

import android.support.v7.widget.RecyclerView
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.model.BookUI

/**
 * Created by pedro on 02/01/18.
 */
interface MainContract {

    interface View {
        fun createButtonListeners()
        fun loadHighlightedBookId(): String?
        fun createAdapter()

        fun getViewBookList(): LinkedHashMap<String, BookUI>
        fun getViewAdapter(): RecyclerView.Adapter<*>?
        fun getViewBinding(): ActivityMainBinding
    }

    interface Presenter {
        fun loadHighlighted(bookId: String?)
        fun loadAllBooks()
    }
}