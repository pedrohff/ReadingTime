package com.readingtime.ui.main

import android.support.v7.widget.CardView
import android.view.View
import com.readingtime.model.repository.UserBookRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    private val disposable = CompositeDisposable()

    override fun loadHighlighted(bookId: String?, cardView: CardView) {
        if (bookId != null) {
            if(cardView.visibility == View.GONE)
                cardView.visibility = View.VISIBLE
            view.createSkeletonCardView()
            disposable.add(
                    UserBookRepository.getLastUserbook()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ bookPres ->
                                view.updateHighlighted(bookPres)
                            }, { e ->
                                e.printStackTrace()
                            })
            )
        } else {
            cardView.visibility = View.GONE
        }

    }

    override fun loadAllBooks() {
        disposable.add(
                UserBookRepository.getUserBooks("pedro")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ uBooks ->
                            view.addBooksToAdapter(uBooks)
                        }, { e ->
                            e.printStackTrace()
                        }, {
                            view.updateAdapter()
                        })
        )
    }

//    override fun loadAllBooks() {
//        disposable.add(
//                RemoteUserBook.listAll()
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ uBook ->
//                            view.addBookToAdapter(uBook)
//                        }, { e ->
//                            e.printStackTrace()
//                        }, {
//                            view.updateAdapter()
//                        })
//        )
//    }

    override fun subscribe(bookId: String?, cardView: CardView) {
        loadHighlighted(bookId, cardView)
        loadAllBooks()
    }

    override fun unsubscribe() {
        disposable.clear()
    }

}