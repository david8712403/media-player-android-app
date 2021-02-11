package com.davidchen.mediaplayer

import android.R.string.ok
import android.app.AlertDialog
import android.content.Context
import android.os.Looper

class AlertDialogUtil {
    companion object {
        var mAlertDialog: AlertDialog? = null

        fun showAlertDialog(context: Context, msg: String) {
            Looper.prepare()
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            val alert = builder
                    .setTitle("Error")
                    .setMessage(msg)
                    .setPositiveButton(ok) { dialog, _ -> dialog?.dismiss() }
                    .create()
            alert.show()
            Looper.loop()
        }

        fun dismiss() {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
        }
    }
}