package com.readingtime

import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import com.readingtime.model.Preferences

/**
 * Created by phffeitosa on 14/03/18.
 */

object ChronometerController {

    var running: Boolean = false
    var startTime: Long = 0
    var timeCounterMilis: Long = 0

    fun start(bookId: String, chronometer: Chronometer) {
        var baseFromPreferences:Long = 0

        if(!running) {
            val sharedPref = ApplicationContextProvider.context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(Preferences.LAST_BOOK.desc, bookId)
            editor.apply()

            baseFromPreferences = loadPreference(bookId)
            startTime = SystemClock.elapsedRealtime()
        }


        timeCounterMilis = SystemClock.elapsedRealtime() + baseFromPreferences
        chronometer.start()
    }

    fun pause(bookId: String, chronometer: Chronometer) {
        timeCounterMilis = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
    }

    fun stop(bookId: String) {
        timeCounterMilis = 0
        running = false
        putPreference(bookId)
    }

    private fun putPreference(bookId: String) {
        val sharedPref = ApplicationContextProvider.context.getSharedPreferences("ChronometerController", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(bookId + "-timeCounterMilis", timeCounterMilis)
        editor.apply()
    }

    private fun loadPreference(bookId: String): Long {
        val sharedPref = ApplicationContextProvider.context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        return sharedPref.getLong((bookId + "-timeCounterMilis"), 0)
    }

}