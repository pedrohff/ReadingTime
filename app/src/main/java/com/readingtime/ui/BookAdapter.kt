package com.readingtime.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.readingtime.R
import com.readingtime.extensions.inflate
import com.readingtime.model.BookPresenter

/**
 * Created by pedro on 31/12/17.
 */



class BookAdapter(private val books: MutableList<BookPresenter>, private val context: Context, var listener: OnClickListener ) : RecyclerView.Adapter<BookAdapter.BookHolder>(){
    interface OnClickListener {
        fun onItemClick(item: BookPresenter)
    }
    override fun getItemCount(): Int {
        return books.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookAdapter.BookHolder {
        val inflatedView = parent!!.inflate(R.layout.item_book)
        return BookHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BookAdapter.BookHolder?, position: Int) {
        val book = books[position]
        if(holder !=null && book.book!=null){
            holder.bindView(book, listener)
        }
    }


    class BookHolder(v: View) : RecyclerView.ViewHolder(v) {
//        override fun onClick(v: View?) {
//            var layout: CoordinatorLayout? = v?.findViewById(R.id.activity_main)
//
//            if(layout!=null) {
//                Snackbar.make(layout, adapterPosition.toString(), Snackbar.LENGTH_SHORT)
//            }
//
//        }

        fun bindView(book: BookPresenter, listener: OnClickListener) {
            presenter = book
            bookName.text = book.book?.name
            bookHours.text = book.timeRead
            bookPercentage.text = book.percentage

            this.itemView.setOnClickListener {
                listener.onItemClick(presenter)
            }
        }

        lateinit var presenter: BookPresenter
        val bookName: TextView = v.findViewById(R.id.tvItemBookName)
        val bookPercentage: TextView = v.findViewById(R.id.tvItemBookPerc)
        val bookHours: TextView = v.findViewById(R.id.tvItemBookHours)

    }
}


