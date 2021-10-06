package com.dlex.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.currency_cell.view.*
import com.dlex.Activity.CurrencyController
import com.dlex.Helper.CircleImage
import com.dlex.Model.CurrencyData

class CurrencyAdapter(val context: Context, val layout: Int, val currencyList: List<CurrencyData>): RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val view = layoutInflater.inflate(layout, null)
    private val circleImage = CircleImage
    private val currencyController: CurrencyController = context as CurrencyController

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val viewCell = LayoutInflater.from(p0.context).inflate(layout, p0, false)
        return ViewHolder(viewCell)
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val imgString = currencyList[p1].code.toLowerCase()
        val imageID = context.resources.getIdentifier(imgString, "drawable", context.packageName)
        circleImage.setImage(view, imageID, p0.currencyImage)
        p0.currencyName.text = currencyList[p1].currency
        p0.itemView.setOnClickListener {
            currencyController.currencyISO = currencyList[p1].code
            currencyController.loadDataBack()
            currencyController.finish()
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val currencyName = view.txtCurrencyName
        val currencyImage = view.imgCurrencyFlag
    }
}

