package com.readingtime.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.readingtime.extensions.millisToString
import java.util.*

/**
 * Created by pedro on 05/01/18.
 */
data class UserBook(var id: String = "",
                    @get:Exclude var book: Book = Book(),
                    var lastVisit: Long = Date().time,
                    var timeRead: Long = 0,
                    var status: UserBookStatus = UserBookStatus.NEW,
                    var dateAdded: Long = Date().time,
                    var dateFinished: Long? = null,
                    var pageStopped: Int = 0,
                    @get:Exclude var records: MutableList<Record>? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Book::class.java.classLoader),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readSerializable() as UserBookStatus,
            parcel.readLong(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readInt())

    fun getPerc(): Int = (pageStopped * 100 / book.pages).toInt()

    fun getPercString(): String = getPerc().toString() + "%"

    fun getTimeString(): String = millisToString(timeRead)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(book, flags)
        parcel.writeLong(lastVisit)
        parcel.writeLong(timeRead)
        parcel.writeSerializable(status)
        parcel.writeLong(dateAdded)
        parcel.writeValue(dateFinished)
        parcel.writeInt(pageStopped)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBook> {
        override fun createFromParcel(parcel: Parcel): UserBook {
            return UserBook(parcel)
        }

        override fun newArray(size: Int): Array<UserBook?> {
            return arrayOfNulls(size)
        }
    }

}

class FirebaseUserBook(
        var id: String,
        var lastVisit: Long,
        var timeRead: Long,
        var status: String,
        var dateAdded: Long,
        var dateFinished: Long?,
        var pageStopped: Int) {

    fun toUserBook(): UserBook {
        return UserBook(
                id = this.id,
                lastVisit = this.lastVisit,
                timeRead = this.timeRead,
                status = UserBookStatus.valueOf(this.status),
                dateAdded = this.dateAdded,
                dateFinished = this.dateFinished,
                pageStopped = this.pageStopped
        )
    }
}
