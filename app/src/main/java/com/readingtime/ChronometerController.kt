package com.readingtime

import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.readingtime.model.Preferences

/**
 * Created by phffeitosa on 14/03/18.
 */

object ChronometerController {

    enum class Status {
        RUNNING,
        PAUSED,
        STOPPED
    }

    var status: Status = Status.STOPPED
    var startTime: Long = 0
    var timeCounterMilis: Long = 0

    fun start(bookId: String, chronometer: Chronometer) {
        var baseFromPreferences:Long = 0
        startTime = SystemClock.elapsedRealtime()

        if(status == Status.STOPPED) {
            val sharedPref = ApplicationContextProvider.context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(Preferences.LAST_BOOK.desc, bookId)
            editor.apply()
            timeCounterMilis = loadPreference(bookId)
        }

        chronometer.base = SystemClock.elapsedRealtime() + timeCounterMilis

        chronometer.start()
        status = Status.RUNNING
    }

    fun pause(bookId: String, chronometer: Chronometer) {
        storeTimeCounter(bookId, chronometer)
        chronometer.stop()
        status = Status.PAUSED
    }

    fun stop(bookId: String, chronometer: Chronometer) {
        timeCounterMilis = 0
        storeTimeCounter(bookId, chronometer)
        chronometer.base = 0
        chronometer.stop()
        status = Status.STOPPED
    }

    fun storeTimeCounter(bookId: String, chronometer: Chronometer) {
        timeCounterMilis = chronometer.base - SystemClock.elapsedRealtime()
        val sharedPref = ApplicationContextProvider.context.getSharedPreferences("ChronometerController", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(bookId + "-timeCounterMilis", timeCounterMilis)
        editor.apply()
    }

    private fun loadPreference(bookId: String): Long {
        val sharedPref = ApplicationContextProvider.context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPref.getLong((bookId + "-timeCounterMilis"), 0)
    }

    fun isRunning() : Boolean = (status == Status.RUNNING)

    fun isPaused(): Boolean = (status == Status.PAUSED)

    fun isStopped(): Boolean = (status == Status.STOPPED)

}