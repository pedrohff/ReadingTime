package com.readingtime.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by pedro on 26/12/17.
 */
@Entity
data class Record(
        @PrimaryKey
        @ColumnInfo(name = "record_id")
        var id: String,
        var bookId: String,
        var date: Long,
        var dateString: String,
        var milisRead: Long,
        var pagesRead: Int,
        var pageStopped: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(bookId)
        parcel.writeLong(date)
        parcel.writeString(dateString)
        parcel.writeLong(milisRead)
        parcel.writeInt(pagesRead)
        parcel.writeInt(pageStopped)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }

        fun construct(book: Book, time:Long, lastRecord: Record?, pageStopped:Int, date: Date): Record {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val newDate = calendar.time

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val dateString = formatter.format(date)
            val pRead = if (lastRecord!=null) pageStopped - lastRecord.pageStopped else pageStopped
            return Record("", book.id, newDate.time, dateString, time, pRead, pageStopped)
        }
    }

}