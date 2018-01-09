package com.readingtime.extensions

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.readingtime.ApplicationContextProvider
import com.readingtime.R
import com.readingtime.model.Preferences
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 29/12/17.
 */

fun millisToString(millis: Long?):String {
    if (millis == null || millis == 0L)
        return "00:00:00"

    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}


fun Long.toDate(): Date{
    return Date(this)
}

fun Date.removeHMS(): Date {
    val cal: Calendar = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


fun AppCompatActivity.savePreference(key: Preferences, value: String) {
    val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(key.desc, value)
    editor.apply()
}

fun AppCompatActivity.savePreference(key: Preferences, value: Long) {
    val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putLong(key.desc, value)
    editor.apply()
}

fun AppCompatActivity.loadPreferenceString(preference: Preferences): String? {
    val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    return sharedPref.getString(preference.desc, null)
}

fun AppCompatActivity.loadPreferenceLong(preference: Preferences): Long {
    val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    return sharedPref.getLong(preference.desc, 0L)
}

//Color
fun getPercentageColorId(perc: Int): Int {
    if (perc < 20)
        return R.color.percentage20
    else if (perc < 40)
        return R.color.percentage40
    else if (perc < 60)
        return R.color.percentage60
    else if (perc < 80)
        return R.color.percentage80
    else
        return R.color.percentage100
}

fun getPercentageColor(perc: Int): Int {
    return ContextCompat.getColor(ApplicationContextProvider.context, getPercentageColorId(perc))
}