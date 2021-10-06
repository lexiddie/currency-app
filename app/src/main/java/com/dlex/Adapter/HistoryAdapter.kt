package com.dlex.Adapter


import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.history_cell.view.*
import com.dlex.Activity.HistoryCalController
import com.dlex.Helper.CircleImage
import com.dlex.Helper.DialogHistory
import com.dlex.Model.History

class HistoryAdapter(val context: Context, val fragment: Fragment, val layout: Int, val historyList: List<History>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val view = layoutInflater.inflate(layout, null)
    private val circleImage = CircleImage
    private val dialogHistory = DialogHistory

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val viewCell = LayoutInflater.from(p0.context).inflate(layout, p0, false)
        return ViewHolder(viewCell)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val imgString = historyList[p1].currencyISO.toLowerCase()
        val imageID = context.resources.getIdentifier(imgString, "drawable", context.packageName)
        val name = historyList[p1].currencyISO + " - " + historyList[p1].date
        circleImage.setImage(view, imageID, p0.currencyFlag)
        p0.currencyCode.text = historyList[p1].currencyISO
        p0.currencyDate.text = historyList[p1].date
        p0.itemView.setOnClickListener {
            val intent = Intent(context, HistoryCalController::class.java)
            intent.putExtra("subKey", historyList[p1].subKey)
            context.startActivity(intent)
        }
        p0.itemView.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                dialogHistory.show(context, fragment,"Deleting History", "Are you sure you want to delete this history?", name, historyList[p1].subKey)
                return true
            }

        })
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val currencyFlag = view.imgHistoryFlag
        val currencyCode = view.txtHistoryCode
        val currencyDate = view.txtHistoryDate
    }
}