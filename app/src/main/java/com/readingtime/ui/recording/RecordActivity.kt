package com.readingtime.ui.recording

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.readingtime.R
import com.readingtime.databinding.ActivityRecordBinding
import com.readingtime.extensions.loadPreferenceLong
import com.readingtime.extensions.loadPreferenceString
import com.readingtime.extensions.removeHMS
import com.readingtime.extensions.savePreference
import com.readingtime.model.Preferences
import com.readingtime.model.UserBook
import kotlinx.android.synthetic.main.activity_record.*
import java.util.*

class RecordActivity : AppCompatActivity(), PageNumberDialog.NoticeDialogListener, RecordContract.View {

    companion object {
        val BOOK = "BOOKRECORDACTIVITY"
    }

    private var currentDate = Date().removeHMS()
    private var firstClick = true
    private var isRunning = false
    private var timeaux: Long = 0
    private var timecounter: Long = 0

    lateinit var binding: ActivityRecordBinding
    lateinit var presenter: RecordContract.Presenter
    lateinit var uBook: UserBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        presenter = RecordPresenter(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_record)
        uBook = intent.getParcelableExtra(BOOK)

        binding.book = uBook.book

        presenter.loadBookCover(uBook.book.coverURL, ivCover, this)

        createButtonListeners()

        var millisPref: Long = 0
        if (uBook.id == loadPreferenceString(Preferences.LAST_BOOK)) {
            millisPref = Math.abs(loadPreferenceLong(Preferences.LAST_MILLIS))
        }
        counter.base = Math.abs(SystemClock.elapsedRealtime() - millisPref)
        timeaux = Math.abs(millisPref)
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe(uBook.id)
    }


    override fun onStop() {
        super.onStop()
        presenter.unsubscribe()
    }

    //DIALOG
    override fun onDialogPositiveClick(page: Int) {
        if (isRunning) {
            startStopTimer()
        }
        saveRecord(page)
    }

    //PRIVATE
    private fun createButtonListeners() {
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
    }

    private fun showDialog() {
        val dialog = PageNumberDialog()
        dialog.pageString = "(${uBook.pageStopped}/${uBook.book.pages})"
        dialog.show(fragmentManager, "PageNumberDialog")

    }

    private fun startStopTimer() { //TRY TO IMPROVE
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

    private fun saveRecord(pagenum: Int) {
        presenter.saveAll(currentDate, timecounter, pagenum, uBook, {
            Toast.makeText(this@RecordActivity, "Record Saved", Toast.LENGTH_SHORT).show()
            savePreference(Preferences.LAST_MILLIS, 0)
            finish()
        })

    }

}

