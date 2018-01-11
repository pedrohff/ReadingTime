package com.readingtime.ui.main

import android.support.v7.widget.CardView
import android.view.View
import com.readingtime.model.UserBook
import com.readingtime.model.remote.RemoteUserBook
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    private val disposable = CompositeDisposable()

    override fun loadHighlighted(bookId: String?, cardView: CardView) {
        lateinit var bookAux: UserBook
        if (bookId != null) {
            if(cardView.visibility == View.GONE)
                cardView.visibility = View.VISIBLE
            view.createSkeletonCardView()
            disposable.add(
                    RemoteUserBook.findById(bookId = bookId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ bookPres ->
                                bookAux = bookPres
                            }, { e ->
                                e.printStackTrace()
                            }, {
                                view.updateHighlighted(bookAux)
                            })
            )
        } else {
            cardView.visibility = View.GONE
        }

    }

    override fun loadAllBooks() {
        disposable.add(
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

    override fun subscribe(bookId: String?, cardView: CardView) {
        loadHighlighted(bookId, cardView)
        loadAllBooks()
    }

    override fun unsubscribe() {
        disposable.clear()
    }

}