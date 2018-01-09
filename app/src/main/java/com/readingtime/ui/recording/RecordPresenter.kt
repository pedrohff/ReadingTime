package com.readingtime.ui.recording

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.ImageView
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import com.readingtime.model.UserBookStatus
import com.readingtime.model.remote.FirebaseProvider
import com.squareup.picasso.Picasso
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */

class RecordPresenter : RecordContract.Presenter {

    lateinit var lastRecord: Record
    val subscriptions = CompositeSubscription()

    override fun subscribe(bookId: String) {
        loadLastRecord(bookId)
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }


    fun loadLastRecord(bookId: String) {
        subscriptions.add(
                FirebaseProvider.findLastRecord(bookId = bookId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ record ->
                            lastRecord = record
                        })
        )
    }

    override fun loadBookCover(url: String, view: ImageView, context: Context) {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(context)
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .into(view)
        } else {
//            TODO("PLACEHOLDER IMG")
        }
    }

    override fun updateUserBook(uBook: UserBook, pagenum: Int, time: Long) {
        uBook.pageStopped = pagenum
        uBook.lastVisit = Date().time
        uBook.timeRead += time

        if (uBook.pageStopped == uBook.book.pages) {
            uBook.dateFinished = Date().time
            uBook.status = UserBookStatus.READ
        } else {
            uBook.status = UserBookStatus.READING
        }
        FirebaseProvider.saveUserBook(uBook)
    }

    override fun saveRecord(currentDate: Date, time: Long, pagenum: Int, uBook: UserBook) {
        var record: Record
        if (lastRecord.date == currentDate.time) {
            record = lastRecord
            record.milisRead += time
            record.pagesRead += pagenum - record.pageStopped
            record.pageStopped = pagenum
        } else {
            record = Record.construct(uBook.book, time, lastRecord, pagenum, currentDate)
        }

        FirebaseProvider.saveRecord(record, uBook.id)
    }
}