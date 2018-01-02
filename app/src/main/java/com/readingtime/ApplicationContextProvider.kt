package com.readingtime

import android.app.Application
import android.content.Context


/**
 * Created by pedro on 02/01/18.
 */
class ApplicationContextProvider : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

    }

    companion object {

        lateinit var context: Context
            private set
    }

}