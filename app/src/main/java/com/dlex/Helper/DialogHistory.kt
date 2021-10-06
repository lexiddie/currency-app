package com.dlex.Helper

import android.app.AlertDialog
import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.dlex.Fragment.Histories

object DialogHistory {

    fun show(context: Context, fragment: Fragment, title: String, message: String, name: String, subKey: String) {
        val dialog = AlertDialog.Builder(context)
        val savingStandard = SavingStandard(context)
        val singleToast = SingleToast
        dialog.setCancelable(true)
        dialog.setTitle(title)
        dialog.setMessage(message + " $name")
        dialog.setPositiveButton("Delete") { _, id ->
            savingStandard.deletingSaveList(subKey)
            singleToast.show(context, "$name has been deleted", Toast.LENGTH_LONG)
            val refreshFragment = fragment as Histories
            refreshFragment.refreshAdapter()
        }.setNegativeButton("Cancel ") { _, which ->

        }
        val alert = dialog.create()
        alert.show()
    }
}