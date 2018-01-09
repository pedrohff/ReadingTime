package com.readingtime.ui.main

import com.readingtime.model.UserBook
import com.readingtime.model.remote.RemoteUserBook
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    val subscriptions = CompositeSubscription()

    override fun loadHighlighted(bookId: String?) {
        lateinit var bookAux: UserBook
        if (bookId != null) {
            subscriptions.add(
                    RemoteUserBook.findById(bookId = bookId).subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe({ bookPres ->
                                bookAux = bookPres
                            }, { e ->
                                e.printStackTrace()
                            }, {
                                view.updateHighlighted(bookAux)
                            })
            )
        }

    }

    override fun loadAllBooks() {
        subscriptions.add(
                RemoteUserBook.listAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ uBook ->
                            view.addBookToAdapter(uBook)
                        }, { e ->
                            e.printStackTrace()
                        }, {
                            view.updateAdapter()
                        })
        )
    }

    override fun subscribe(bookId: String?) {
        loadHighlighted(bookId)
        loadAllBooks()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

}