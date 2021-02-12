package com.davidchen.mediaplayer.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.davidchen.mediaplayer.R

class ProgressDialogUtil {
    companion object {
        var mAlertDialog: AlertDialog? = null

        fun showProgressDialog(context: Context, msg: String) {
            if (mAlertDialog == null) {
                mAlertDialog = AlertDialog.Builder(context, R.style.CustomProgressDialog).create()
            }else {
                mAlertDialog!!.findViewById<TextView>(R.id.tv_load_msg).text = msg
            }
            val loadView = LayoutInflater
                .from(context)
                .inflate(R.layout.progress_dialog_view, null)

            mAlertDialog!!.setView(loadView, 0,0,0,0)
            mAlertDialog!!.setCanceledOnTouchOutside(false)
            mAlertDialog!!.setCancelable(false)

            if (!mAlertDialog!!.isShowing) {
                mAlertDialog!!.show()
            }
        }

        fun dismiss() {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
        }
    }
}