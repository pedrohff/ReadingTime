package com.readingtime.model

import android.os.Parcel
import android.os.Parcelable
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 26/12/17.
 */
data class Book(var id:String="",
                var name:String="",
                var author:String="",
                var artist:String?=null,
                var publisher:String="",
                var type:String="",
                var pages:Int=0,
                var category:String="") : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(author)
        parcel.writeString(artist)
        parcel.writeString(publisher)
        parcel.writeString(type)
        parcel.writeInt(pages)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}

data class BookPresenter (var book: Book?,
                          var lastRecord: Record?,
                          var timeRead: String,
                          var percentage: String){
    companion object {
        fun construct(book: Book?, records: List<Record>?): BookPresenter {
            var lastRecord = records?.last()
            var percentage = "0%"
//            if(book!=null && lastRecord!=null)
//                percentage = String.format("%d%", lastRecord.pageStopped*100/book.pages)
            var millis: Long? = records?.fold(0, {acc:Long, record -> acc+record.milisRead })
            var timeString = millisToString(millis)
            return BookPresenter(book,records?.last(), timeString, percentage)
        }

        fun millisToString(millis: Long?):String {
            var hour = TimeUnit.HOURS.toMillis(1)
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
    }
}