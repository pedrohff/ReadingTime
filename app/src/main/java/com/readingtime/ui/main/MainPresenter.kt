package com.readingtime.ui.main

import com.readingtime.model.UserBook
import com.readingtime.model.remote.FirebaseProvider
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    val api = FirebaseProvider

    override fun loadHighlighted(bookId: String?): Subscription? {
        lateinit var bookAux: UserBook
        if (bookId != null) {
            return api.findUserBook(bookId = bookId).subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ bookPres ->
                        bookAux = bookPres
                    }, { e ->
                        e.printStackTrace()
                    }, {
                        view.getViewBinding().uBook = bookAux
                        view.loadHighlightedImage(bookAux.book.coverURL)
                        view.updateHighlightedPercentage(bookAux.getPerc())
                    })
        }

        return null
    }

    override fun loadAllBooks(): Subscription? {
        return api.listUserBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uBook ->
                    view.getViewBookMap().put(uBook.id, uBook)
                }, { e ->
                    e.printStackTrace()
                }, {
                    view.updateAdapter()
                })
    }

}