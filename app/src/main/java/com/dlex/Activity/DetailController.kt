package com.dlex.Activity

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dlex.Helper.SingleDate
import com.dlex.Helper.SingleToast
import com.dlex.R

class DetailController : AppCompatActivity() {

    private lateinit var standard: SharedPreferences
    private lateinit var txtAlertName: TextView
    private lateinit var txtCreatedDate: TextView
    private lateinit var txtFoundDate: TextView
    private lateinit var alertName: String
    private lateinit var alertDate: String
    private lateinit var createDate: String
    private lateinit var handleHideFound: LinearLayout
    private var alertCheck: Boolean = false
    val singleDate = SingleDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        standard = this.getSharedPreferences("mainCurrency", 0)
        txtAlertName = findViewById(R.id.txtAlertName)
        txtCreatedDate = findViewById(R.id.txtAlertDate)
        txtFoundDate = findViewById(R.id.txtFoundedDate)
        handleHideFound = findViewById(R.id.handleHideFound)
        initializeMain()
    }


    private fun initializeMain() {
        alertName = intent.getStringExtra("alertName")
        alertDate = intent.getStringExtra("alertDate")
        createDate = intent.getStringExtra("createdDate")
        alertCheck = intent.getBooleanExtra("alertCheck", false)

        txtAlertName.text = alertName
        txtCreatedDate.text = singleDate.updatingDisplay(createDate)

        if (alertCheck) {
            val formatDate = singleDate.updatingDate(alertDate)
            txtFoundDate.text = singleDate.updatingDisplay(formatDate)
        } else {
            handleHideFound.visibility = View.GONE
        }

        Log.e("Alert", alertName)
        Log.e("Alert", alertDate)
        Log.e("Alert", createDate)
        Log.e("Alert", alertCheck.toString())


    }



    fun handleDismiss(view: View) {
        finish()
    }
}
