package com.readingtime.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import com.readingtime.R
import kotlinx.android.synthetic.main.dialog_numpage.*

/**
 * Created by pedro on 28/12/17.
 */
class PageNumberDialog() : DialogFragment() {

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, pagenum:Int)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    lateinit var mListener: NoticeDialogListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            mListener = context as NoticeDialogListener
        }catch (e:ClassCastException) {
            throw ClassCastException(context.toString() + " must implement NoticeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        var inflater: LayoutInflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_numpage, null))
                .setPositiveButton("Confirm", DialogInterface.OnClickListener {
                    dialog, which ->  run{
                        mListener.onDialogPositiveClick(this, Integer.parseInt(etPageStopped.text.toString()))
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, which -> this.dialog.cancel()
                })

        return builder.create()
    }


}