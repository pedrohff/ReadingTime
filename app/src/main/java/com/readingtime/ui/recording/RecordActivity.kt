package com.readingtime.ui.recording

import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.google.firebase.database.FirebaseDatabase
import com.readingtime.ApplicationContextProvider.Companion.context
import com.readingtime.R
import com.readingtime.databinding.ActivityRecordBinding
import com.readingtime.extensions.loadPreferenceLong
import com.readingtime.extensions.loadPreferenceString
import com.readingtime.extensions.removeHMS
import com.readingtime.extensions.savePreference
import com.readingtime.model.Book
import com.readingtime.model.Preferences
import com.readingtime.model.Record
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_record.*
import java.util.*

class RecordActivity : AppCompatActivity(), PageNumberDialog.NoticeDialogListener {

    companion object {
        val BOOK = "BOOKRECORDACTIVITY"
        val RECORD = "RECORDRECORDACTIVITY"
    }

    var isRunning = false
    lateinit var book: Book
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
        book = intent.getParcelableExtra(BOOK)
        lastRecord = intent.getParcelableExtra(RECORD)

        binding.book = book

        loadBookCover(book.coverURL)

        btStartStop.setOnClickListener { startStopTimer() }
        btSend.setOnClickListener {
            startStopTimer()
            showDialog()
        }
        btCancel.setOnClickListener {
            savePreference(Preferences.LAST_MILLIS, 0)
            finish()
        }

        var millisPref: Long = 0
        if (book.id == loadPreferenceString(Preferences.LAST_BOOK)) {
            millisPref = loadPreferenceLong(Preferences.LAST_MILLIS)
        }
        counter.base = SystemClock.elapsedRealtime() - millisPref
        timeaux = millisPref
    }

    fun startStopTimer() {

        if(firstClick) {
            savePreference(Preferences.LAST_BOOK, book.id)
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
            record = Record.construct(book, timecounter, lastRecord, pagenum, this.currentDate)
        }

        val db = FirebaseDatabase.getInstance().getReference("records")
        db.child(book.id).child(record.id).setValue(record)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
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
        var dialog = PageNumberDialog()
        dialog
        dialog.show(fragmentManager, "PageNumberDialog")
    }

    fun loadBookCover(url: String) {
        var img = ImageView(this)
        var width = Resources.getSystem().displayMetrics.widthPixels
        var height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != null && url != "") {
            Picasso.with(this)
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .into(ivCover)
        } else {
            TODO("PLACEHOLDER IMG")
        }
    }
}

