package com.readingtime.view

import android.app.DialogFragment
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.readingtime.R
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
    var currentDate = Date()

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
//        if(!counterStarted){
//            counterStarted = true
//            counter.base = SystemClock.elapsedRealtime()
//        }

        if (isRunning){
            timeaux = counter.base - SystemClock.elapsedRealtime()
            counter.stop()
            btStartStop.text = "Resume"
            isRunning = !isRunning
        } else {
            counter.base = SystemClock.elapsedRealtime() + timeaux
            counter.start()
            btStartStop.text = "Pause"
            isRunning = !isRunning
        }

        timecounter = ((SystemClock.elapsedRealtime() - counter.base).toString()).toLong()
    }


    fun saveRecord(pagenum: Int) {
        if(isRunning){
            startStopTimer()
        }


        var record: Record = Record.construct(book, timecounter, lastRecord, pagenum, currentDate)

        val db = FirebaseDatabase.getInstance().getReference("records")
        db.child(book.id).setValue(record)

    }

    //Dialog
    override fun onDialogPositiveClick(dialog: DialogFragment, pagenum: Int) {
        saveRecord(pagenum)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showDialog() {
        var dialog = PageNumberDialog()
        dialog.show(fragmentManager, "PageNumberDialog")
    }
}

