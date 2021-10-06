package com.dlex.Fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dlex.Adapter.HistoryAdapter
import com.dlex.Helper.SavingStandard
import com.dlex.Helper.SingleDate
import com.dlex.Model.History
import com.dlex.R


class Histories : Fragment() {

    lateinit var mainController: AppCompatActivity
    private lateinit var standard: SharedPreferences
    private lateinit var superView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var sortedList: List<History>
    private var historyList = mutableListOf<History>()
    private val singleData = SingleDate()

    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        standard = mainController.getSharedPreferences("mainCurrency", 0)
        initializeData()
        superView = inflater.inflate(R.layout.fragment_histories, container, false)

        recyclerView = superView.findViewById(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(mainController, LinearLayout.VERTICAL, false)

        refreshAdapter()

        return superView
    }


    private fun initializeData() {
        historyList.clear()
        val currentList = HashSet<String>()
        val body = standard.getStringSet("savingList", null)
        if (body != null) {
            currentList.addAll(body)
            for (i in currentList) {
                val mainSplit = i.split(" ")
                val dateSplit = mainSplit[0].split("-")
                historyList.add(History(singleData.updatingDisplay(mainSplit[0]), dateSplit[2], dateSplit[1], dateSplit[0], mainSplit[1], i))
            }
        }
        sortedList = historyList.sortedWith(compareBy(History::year, History::month, History::day))

    }

    fun refreshAdapter() {
        initializeData()
        recyclerView.visibility = View.VISIBLE
        if (sortedList.isNotEmpty()) {
            adapter = HistoryAdapter(mainController, this, R.layout.history_cell, sortedList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        } else {
            recyclerView.visibility = View.GONE
        }
    }


}
