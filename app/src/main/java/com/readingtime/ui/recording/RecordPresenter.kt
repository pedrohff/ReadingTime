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
import com.readingtime.model.repository.RecordRepository
import com.readingtime.model.repository.UserBookRepository
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
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
                RecordRepository.findLast(bookId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ record ->
                            lastRecord = record
                        },{
                            e -> e.printStackTrace()
                        })
        )
    }

    override fun loadBookCover(url: String, view: ImageView, context: Context) {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(context)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(width, height)
                    .centerCrop()
                    .into(view, object: Callback{
                        override fun onSuccess() {
                        }

                        override fun onError() {
                            Picasso.with(context)
                                    .load(url)
                                    .resize(width, height)
                                    .centerCrop()
                                    .into(view)
                        }

                    })
        } else {
//            TODO("PLACEHOLDER IMG")
        }
    }

    private fun updateUserBook(uBook: UserBook, pagenum: Int, time: Long, onComplete: () -> Unit = {}) {
        uBook.pageStopped = pagenum
        uBook.lastVisit = Date().time
        uBook.timeRead += time

        if (uBook.pageStopped == uBook.book.pages) {
            uBook.dateFinished = Date().time
            uBook.status = UserBookStatus.READ
        } else {
            uBook.status = UserBookStatus.READING
        }
        UserBookRepository.update(uBook, onComplete = onComplete)
        Crashlytics.log("RecordView UserBook Updated")
    }

    private fun saveRecord(currentDate: Date, time: Long, pagenum: Int, uBook: UserBook, onComplete: () -> Unit = {}) {
        val record: Record

        if (lastRecord.date == currentDate.time) {
            record = lastRecord
            record.milisRead += time
            record.pagesRead += pagenum - record.pageStopped
            record.pageStopped = pagenum
        } else {
            record = Record.construct(uBook.book, time, lastRecord, pagenum, currentDate)
        }
        //TODO UserRepository.save || update
        RecordRepository.save(record, uBook.id, onComplete = onComplete)
    }

    //TODO whats this function for?
    override fun saveAll(currentDate: Date, timecounter: Long, pagenum: Int, uBook: UserBook, onComplete: () -> Unit) {
        saveRecord(currentDate, timecounter, pagenum, uBook, {
            updateUserBook(uBook, pagenum, timecounter, {
                onComplete()
            })
        })
    }
}