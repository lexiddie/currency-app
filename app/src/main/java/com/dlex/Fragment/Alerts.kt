package com.dlex.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import com.dlex.Activity.CreateAlertController
import com.dlex.Adapter.AlertAdapter
import com.dlex.Helper.AlertNotification
import com.dlex.Model.Alert
import com.dlex.R


class Alerts : Fragment() {

    lateinit var mainController: AppCompatActivity
    lateinit var handleGesture: Button
    private lateinit var standard: SharedPreferences
    private lateinit var superView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlertAdapter
    private lateinit var sortedList: List<Alert>
    private var alertList = mutableListOf<Alert>()


    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        standard = mainController.getSharedPreferences("mainCurrency", 0)
        superView = inflater.inflate(R.layout.fragment_alerts, container, false)

        recyclerView = superView.findViewById(R.id.recyclerAlert)
        recyclerView.layoutManager = LinearLayoutManager(mainController, LinearLayout.VERTICAL, false)


        handleGesture.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(mainController, CreateAlertController::class.java)
                mainController.startActivity(intent)
            }

        })

        refreshAdapter()

        return superView
    }

    private fun initializeData() {
        alertList.clear()
        val gson = GsonBuilder().create()
        val body = standard.getStringSet("alertList", null)
        if (body != null) {
            for (i in body) {
                val element = standard.getString(i, null)
                alertList.add(gson.fromJson(element, Alert::class.java))
            }
        }
        sortedList = alertList.sortedWith(compareBy(Alert::year, Alert::month, Alert::day))
    }

    fun refreshAdapter() {
        initializeData()
        recyclerView.visibility = View.VISIBLE
        if (sortedList.isNotEmpty()) {
            adapter = AlertAdapter(mainController, this, R.layout.alert_cell, sortedList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            recyclerView.visibility = View.GONE
        }
    }

}
