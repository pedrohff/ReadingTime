package com.readingtime.ui.main

import android.support.v7.widget.CardView
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
        fun subscribe(bookId: String?, cardView: CardView)
        fun unsubscribe()
        fun loadAllBooks()
        fun loadHighlighted(bookId: String?, cardView: CardView)
    }
}