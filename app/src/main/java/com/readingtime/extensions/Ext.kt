package com.readingtime.extensions

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 29/12/17.
 */

fun millisToString(millis: Long?):String {
    val hour = TimeUnit.HOURS.toMillis(1)
    if(millis==null)
        return "0:00min"

    if(millis > hour) {
        return String.format("%d:%dh",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
        )
    } else {
        return String.format("%d:%dmin",
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
