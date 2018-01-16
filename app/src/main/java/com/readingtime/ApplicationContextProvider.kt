package com.readingtime

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.local.LocalService


/**
 * Created by pedro on 02/01/18.
 */
class ApplicationContextProvider : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        LocalProvider.db = Room.databaseBuilder(context, LocalService::class.java, "LocalDB").build()
    }

    companion object {
        lateinit var context: Context
            private set
    }

}