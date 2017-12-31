package com.readingtime.view

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.readingtime.R
import com.readingtime.extensions.removeHMS
import com.readingtime.model.Book
import com.readingtime.model.Record
import com.readingtime.ui.PageNumberDialog
import kotlinx.android.synthetic.main.activity_record.*
import java.util.*

class RecordActivity : AppCompatActivity(), PageNumberDialog.NoticeDialogListener {

    var isRunning = false
    lateinit var book: Book
    lateinit var lastRecord: Record
    var timeaux:Long = 0
    var timecounter:Long = 0
    var currentDate = Date().removeHMS()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        book = intent.getParcelableExtra("BOOK")
        lastRecord = intent.getParcelableExtra("RECORD")

        btStartStop.setOnClickListener { startStopTimer() }
        btSend.setOnClickListener { showDialog() }
        btCancel.setOnClickListener { finish() }
    }

    fun startStopTimer() {

        if (isRunning){
            timeaux = counter.base - SystemClock.elapsedRealtime()
            counter.stop()
            btStartStop.text = "Resume" //TODO("strings.xml")
            isRunning = !isRunning
        } else {
            counter.base = SystemClock.elapsedRealtime() + timeaux
            counter.start()
            btStartStop.text = "Pause" //TODO("strings.xml")
            isRunning = !isRunning
        }

        timecounter = ((SystemClock.elapsedRealtime() - counter.base).toString()).toLong()
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

