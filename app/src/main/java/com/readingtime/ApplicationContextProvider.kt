package com.readingtime

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.jakewharton.picasso.OkHttp3Downloader
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.local.LocalService
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.services.settings.IconRequest.build




/**
 * Created by pedro on 02/01/18.
 */
class ApplicationContextProvider : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        LocalProvider.db = Room.databaseBuilder(context, LocalService::class.java, "LocalDB").build()

        var builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }

    companion object {
        lateinit var context: Context
            private set
    }

}