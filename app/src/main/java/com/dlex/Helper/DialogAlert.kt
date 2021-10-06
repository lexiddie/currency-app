package com.dlex.Helper

import android.app.AlertDialog
import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.dlex.Fragment.Alerts

object DialogAlert {

    fun show(context: Context, fragment: Fragment, title: String, message: String, alertName: String) {
        val dialog = AlertDialog.Builder(context)
        val savingStandard = SavingStandard(context)
        val singleToast = SingleToast
        dialog.setCancelable(true)
        dialog.setTitle(title)
        dialog.setMessage(message + " $alertName")
        dialog.setPositiveButton("Delete") { _, id ->
            savingStandard.deletingAlertList(alertName)
            singleToast.show(context, "$alertName has been deleted", Toast.LENGTH_LONG)
            val refreshFragment = fragment as Alerts
            refreshFragment.refreshAdapter()
        }.setNegativeButton("Cancel ") { _, which ->

        }
        val alert = dialog.create()
        alert.show()
    }
}