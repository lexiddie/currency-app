package com.dlex.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_cell.view.*
import com.dlex.Helper.CircleImage
import com.dlex.Model.CurrencyValue
import java.text.DecimalFormat

class HomeAdapter(val context: Context, var value: String, val layout: Int, val currencyList: List<CurrencyValue>): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private val view = layoutInflater.inflate(layout, null)
    private val circleImage = CircleImage

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
        p0.currencyCode.text = currencyList[p1].code
        val decimalFormat = DecimalFormat("###,###.####")
        p0.currencyResult.text = decimalFormat.format(value.toDouble() * currencyList[p1].value)
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val currencyImage = view.imgHomeFlag
        val currencyCode = view.txtHomeCode
        val currencyResult = view.txtHomeResult
    }
}