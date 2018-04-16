package com.readingtime.ui.userbookinfo

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ethanhua.skeleton.Skeleton
import com.readingtime.R
import com.readingtime.databinding.FragmentUserBookInfoBinding
import com.readingtime.model.UserBook
import com.readingtime.ui.recording.RecordActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_main.*

class UserBookInfoFragment : Fragment() {

    lateinit var binding: FragmentUserBookInfoBinding
    // Store instance variables
    private lateinit var userBook: UserBook
    private var page: Int = 1

    companion object {
        fun newInstance(page: Int, userBook: UserBook): UserBookInfoFragment {
            val ubiFragment = UserBookInfoFragment()
            val args = Bundle()
            args.putInt("someInt", page)
            args.putParcelable("userBook", userBook)
            ubiFragment.arguments = args
            return ubiFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_book_info, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_book_info, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun loadHighlightedImage(bookAux: UserBook) {
        val url = bookAux.book.coverURL
        val img = ImageView(activity)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()

        val cvCurrent: CardView? = view?.findViewById(R.id.cvCurrentUserBook)

        if (url != "") {
            Picasso.with(activity)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(width, height)
                    .centerCrop()
                    .into(img, object : Callback {
                        override fun onSuccess() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                cvCurrent?.background = img.drawable
                            } else {
                                cvCurrent?.setBackgroundDrawable(img.drawable)
                            }
                        }

                        override fun onError() {
                            Picasso.with(activity)
                                    .load(url)
                                    .resize(width, height)
                                    .centerCrop()
                                    .into(img, object : Callback {
                                        override fun onSuccess() {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                cvCurrent?.background = img.drawable
                                            } else {
                                                cvCurrent?.setBackgroundDrawable(img.drawable)
                                            }
                                        }

                                        override fun onError() {

                                        }

                                    })
                        }

                    })
        } else {
            //TODO("PLACEHOLDER IMG")
        }
    }
}