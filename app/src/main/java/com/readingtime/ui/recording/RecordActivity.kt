package com.readingtime.ui.recording

import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.ImageView
import com.readingtime.R
import com.readingtime.databinding.ActivityRecordBinding
import com.readingtime.extensions.loadPreferenceLong
import com.readingtime.extensions.loadPreferenceString
import com.readingtime.extensions.removeHMS
import com.readingtime.extensions.savePreference
import com.readingtime.model.Preferences
import com.readingtime.model.Record
import com.readingtime.model.UserBook
import com.readingtime.model.UserBookStatus
import com.readingtime.model.remote.FirebaseProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_record.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class RecordActivity : AppCompatActivity(), PageNumberDialog.NoticeDialogListener {

    companion object {
        val BOOK = "BOOKRECORDACTIVITY"
        val RECORD = "RECORDRECORDACTIVITY"
    }

    var isRunning = false
    lateinit var uBook: UserBook
    lateinit var lastRecord: Record
    var timeaux:Long = 0
    var timecounter:Long = 0
    var currentDate = Date().removeHMS()
    lateinit var binding: ActivityRecordBinding
    var firstClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_record)
        uBook = intent.getParcelableExtra(BOOK)

        binding.book = uBook.book

        loadBookCover(uBook.book.coverURL)

        btStartStop.setOnClickListener { startStopTimer() }
        btSend.setOnClickListener {
            if (isRunning) {
                startStopTimer()
            }
            showDialog()
        }
        btCancel.setOnClickListener {
            savePreference(Preferences.LAST_MILLIS, 0)
            finish()
        }

        var millisPref: Long = 0
        if (uBook.id == loadPreferenceString(Preferences.LAST_BOOK)) {
            millisPref = loadPreferenceLong(Preferences.LAST_MILLIS)
        }
        counter.base = SystemClock.elapsedRealtime() - millisPref
        timeaux = millisPref
    }

    override fun onResume() {
        super.onResume()
        FirebaseProvider.findLastRecord(bookId = uBook.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ record ->
                    lastRecord = record
                })
    }

    fun startStopTimer() {

        if(firstClick) {
            savePreference(Preferences.LAST_BOOK, uBook.id)
            firstClick = false
        }

        if (isRunning){
            timeaux = counter.base - SystemClock.elapsedRealtime()
            counter.stop()
            btStartStop.text = getString(R.string.counter_resume)
            isRunning = !isRunning
        } else {
            counter.base = SystemClock.elapsedRealtime() + timeaux
            counter.start()
            btStartStop.text = getString(R.string.counter_pause)
            isRunning = !isRunning
        }

        timecounter = ((SystemClock.elapsedRealtime() - counter.base).toString()).toLong()
        savePreference(Preferences.LAST_MILLIS, timecounter)
    }


    fun saveRecord(pagenum: Int) {
        var record: Record
        if(lastRecord.date == currentDate.time){
            record = lastRecord
            record.milisRead += timecounter
            record.pagesRead += pagenum-record.pageStopped
            record.pageStopped = pagenum
        } else {
            record = Record.construct(uBook.book, timecounter, lastRecord, pagenum, this.currentDate)
        }

        FirebaseProvider.saveRecord(record, uBook.id)

        uBook.pageStopped = pagenum
        uBook.lastVisit = Date().time
        uBook.timeRead = timecounter

        if (uBook.pageStopped == uBook.book.pages) {
            uBook.dateFinished = Date().time
            uBook.status = UserBookStatus.READ
        } else {
            uBook.status = UserBookStatus.READING
        }

        FirebaseProvider.saveUserBook(uBook)

        savePreference(Preferences.LAST_MILLIS, 0)
        finish()
    }

    //Dialog
    override fun onDialogPositiveClick(page: Int) {
        if(isRunning){
            startStopTimer()
        }
        saveRecord(page)
    }

    private fun showDialog() {
        val dialog = PageNumberDialog()
        dialog.show(fragmentManager, "PageNumberDialog")
    }

    fun loadBookCover(url: String) {
        var img = ImageView(this)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(this)
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .into(ivCover)
        } else {
//            TODO("PLACEHOLDER IMG")
        }
    }
}

