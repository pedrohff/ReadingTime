package com.readingtime.ui.main

import android.support.v7.widget.RecyclerView
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.model.UserBook

/**
 * Created by pedro on 02/01/18.
 */
interface MainContract {

    interface View {
        fun createButtonListeners()
        fun loadHighlightedBookId(): String?
        fun createAdapter()

        fun getViewBookList(): LinkedHashMap<String, UserBook>
        fun getViewAdapter(): RecyclerView.Adapter<*>?
        fun getViewBinding(): ActivityMainBinding
        fun loadHighlightedImage(url: String)
        fun updateHighlightedPercentage(percentage: Int)
    }

    interface Presenter {
        fun loadHighlighted(bookId: String?)
        fun loadAllBooks()
    }
}