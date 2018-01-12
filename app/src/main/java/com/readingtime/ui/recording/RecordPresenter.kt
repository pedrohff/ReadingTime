package com.readingtime.ui.recording

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.ImageView
import com.crashlytics.android.Crashlytics
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import com.readingtime.model.UserBookStatus
import com.readingtime.model.remote.RemoteRecord
import com.readingtime.model.remote.RemoteUserBook
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by pedro on 08/01/18.
 */

class RecordPresenter(var view: RecordContract.View) : RecordContract.Presenter {

    private lateinit var lastRecord: Record
    private val disposable = CompositeDisposable()

    override fun subscribe(bookId: String) {
        loadLastRecord(bookId)
        Crashlytics.log("RecordView Subscribed")
    }

    override fun unsubscribe() {
        disposable.dispose() //DISPOSE: Does not accept new disposables
        Crashlytics.log("RecordView Disposed")
    }


    private fun loadLastRecord(bookId: String) {
        disposable.add(
                RemoteRecord.findLast(bookId = bookId)
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
        RemoteUserBook.update(uBook)
        Crashlytics.log("RecordView UserBook Updated")
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

        RemoteRecord.save(record, uBook.id)
    }
}