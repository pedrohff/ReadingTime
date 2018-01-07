package com.readingtime.ui.main

import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.model.UserBook
import rx.Subscription

/**
 * Created by pedro on 02/01/18.
 */
interface MainContract {

    interface View {
        fun createButtonListeners()
        fun loadHighlightedBookId(): String?
        fun createAdapter()

        fun getViewBookMap(): LinkedHashMap<String, UserBook>
        fun getViewBinding(): ActivityMainBinding
        fun loadHighlightedImage(url: String)
        fun updateHighlightedPercentage(percentage: Int)
        fun updateAdapter()
    }

    interface Presenter {
        fun loadHighlighted(bookId: String?): Subscription?
        fun loadAllBooks(): Subscription?
    }
}