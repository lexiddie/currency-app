package com.dlex.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_currency.*
import com.dlex.Adapter.CurrencyAdapter
import com.dlex.Model.CurrencyData
import com.dlex.R
import android.support.v4.widget.SwipeRefreshLayout
import com.dlex.Helper.LoadJson


class CurrencyController : AppCompatActivity() {

    private lateinit var pullToRefresh: SwipeRefreshLayout
    private var loadJson = LoadJson
    private var currencyList = mutableListOf<CurrencyData>()
    var requestType = 0
    var currencyISO = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)
        parseJson()
        checkRequestType()
        val recyclerView = recyclerCurrency
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val sortedCurrencyList = currencyList.sortedWith(compareBy(CurrencyData::currency))
        val adapter = CurrencyAdapter(this, R.layout.currency_cell, sortedCurrencyList)
        recyclerView.adapter = adapter

        pullToRefresh = findViewById(R.id.srlRefresh)
        pullToRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            pullToRefresh.setRefreshing(false)
        }

    }

    private fun checkRequestType() {
        val session = intent.getStringExtra("requestType")
        requestType = session.toInt()
    }

    private fun parseJson() {
        val gson = GsonBuilder().create()
        val json = loadJson.loadJsonFromAsset(this, "Json/CountryCurrency.json")
        if (json != null) {
            val jsonArray = json
            val size = jsonArray.length()
            var count = 0
            while (count < size) {
                val element = jsonArray.getJSONObject(count)
                val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
                count += 1
                currencyList.add(currency)
            }
        }
    }

    fun handleDismiss(view: View) {
        finish()
    }

    fun loadDataBack() {
        val result = Intent()
        if (requestType == 0) {
            result.putExtra("currencyFirst", currencyISO)
        } else {
            result.putExtra("currencySecond", currencyISO)
        }
        setResult(RESULT_OK, result)
    }

}
