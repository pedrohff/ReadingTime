package com.readingtime.extensions

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.readingtime.model.Preferences
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 29/12/17.
 */

fun millisToString(millis: Long?):String {
    val hour = TimeUnit.HOURS.toMillis(1)
    if(millis==null)
        return "0:00min"

    return if (millis > hour) {
        String.format("%d:%dh",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
        )
    } else {
        String.format("%d:%dmin",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }
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

fun AppCompatActivity.loadPreference(key: Int): String? {
    val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    return sharedPref.getString(getString(key),null)
}

fun <T> rx.Observable<T>.subMainThread(onNext: Action1<T>, onError: Action1<Throwable>, onCompleted: Action0) {
    TODO("verify parameters")
    this.observeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(onNext, onError, onCompleted)
}