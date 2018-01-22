package com.readingtime.ui.main

import android.support.v7.widget.CardView
import android.widget.Toast
import com.readingtime.model.UserBook

/**
 * Created by pedro on 02/01/18.
 */
interface MainContract {

    interface View {
        fun updateAdapter()
        fun updateHighlighted(bookAux: UserBook)
        fun addBookToAdapter(book: UserBook)
        fun addBooksToAdapter(book: List<UserBook>)
        fun createSkeletonCardView()
        fun makeToast(message: String, len: Int = Toast.LENGTH_SHORT)
    }

    interface Presenter {
        fun subscribe(cardView: CardView)
        fun unsubscribe()
        fun loadAllBooks()
        fun loadHighlighted(cardView: CardView)
    }
}