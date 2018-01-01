package com.readingtime.ui.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.readingtime.R
import com.readingtime.extensions.inflate
import com.readingtime.model.BookUI

/**
 * Created by pedro on 31/12/17.
 */


class MainAdapter(private val books: MutableList<BookUI>, private var listener: OnClickListener) : RecyclerView.Adapter<MainAdapter.BookHolder>() {
    interface OnClickListener {
        fun onItemClick(item: BookUI)
    }
    override fun getItemCount(): Int {
        return books.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookHolder {
        val inflatedView = parent!!.inflate(R.layout.item_book)
        return BookHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BookHolder?, position: Int) {
        val book = books[position]
        if(holder !=null && book.book!=null){
            holder.bindView(book, listener)
        }
    }


    class BookHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(book: BookUI, listener: OnClickListener) {
            presenter = book
            bookName.text = book.book?.name
            bookHours.text = book.timeRead
            bookPercentage.text = book.percentage

            this.itemView.setOnClickListener {
                listener.onItemClick(presenter)
            }
        }

        lateinit var presenter: BookUI
        val bookName: TextView = v.findViewById(R.id.tvItemBookName)
        val bookPercentage: TextView = v.findViewById(R.id.tvItemBookPerc)
        val bookHours: TextView = v.findViewById(R.id.tvItemBookHours)

    }
}


