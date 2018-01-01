package com.readingtime.ui.recording

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.readingtime.R
import com.readingtime.databinding.ActivityRecordBinding
import com.readingtime.extensions.removeHMS
import com.readingtime.extensions.savePreference
import com.readingtime.model.Book
import com.readingtime.model.Preferences
import com.readingtime.model.Record
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

        btStartStop.setOnClickListener { startStopTimer() }
        btSend.setOnClickListener { showDialog() }
        btCancel.setOnClickListener { finish() }
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
        tvHelper.text = timecounter.toString()
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
        dialog.show(fragmentManager, "PageNumberDialog")
    }
}

