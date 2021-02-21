package com.davidchen.mediaplayer.util

import android.app.AlertDialog
import android.content.ComponentCallbacks
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.TextView
import com.davidchen.mediaplayer.R

class MyProgressDialog(context: Context) {

    private var context: Context? = context
    private var dialog: AlertDialog? = null

    init {
        if (dialog == null) {
            dialog = AlertDialog
                    .Builder(context, R.style.CustomProgressDialog)
                    .create()
            
            val loadView = LayoutInflater
                    .from(context)
                    .inflate(R.layout.progress_dialog_view, null)

            dialog?.setView(loadView, 0,0,0,0)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.setCancelable(false)
        }
    }

    fun show(msg: String) {
        if (dialog == null) {
            dialog = AlertDialog.Builder(context, R.style.CustomProgressDialog).create()
        }
        dialog?.findViewById<TextView>(R.id.tv_load_msg)?.text = msg

        if (!dialog!!.isShowing) {
            dialog!!.show()
        }
    }

    fun dismiss() {
        if (dialog == null) {
            return
        }else if (dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }
}