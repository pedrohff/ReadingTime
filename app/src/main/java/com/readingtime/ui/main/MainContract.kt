package com.readingtime.ui.main

import com.readingtime.model.UserBook

/**
 * Created by pedro on 02/01/18.
 */
interface MainContract {

    interface View {
        fun updateAdapter()
        fun updateHighlighted(bookAux: UserBook)
        fun addBookToAdapter(book: UserBook)
    }

    interface Presenter {
        fun subscribe(bookId: String?)
        fun unsubscribe()
        fun loadHighlighted(bookId: String?)
        fun loadAllBooks()
    }
}