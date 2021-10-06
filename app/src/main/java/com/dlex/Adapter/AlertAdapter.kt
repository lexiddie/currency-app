package com.dlex.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alert_cell.view.*
import com.dlex.Activity.DetailController
import com.dlex.Helper.CircleImage
import com.dlex.Helper.DialogAlert
import com.dlex.Model.Alert
import com.dlex.R
import java.text.DecimalFormat

class AlertAdapter(val context: Context, val fragment: Fragment, val layout: Int, val alertList: List<Alert>): RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val view = layoutInflater.inflate(layout, null)
    private val circleImage = CircleImage
    private val dialogAlert = DialogAlert


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val viewCell = LayoutInflater.from(p0.context).inflate(layout, p0, false)
        return ViewHolder(viewCell)
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val decimalFormat = DecimalFormat("###,###.#####")
        val imgFirstString = alertList[p1].currencyFirst.toLowerCase()
        val imgSecondString = alertList[p1].currencySecond.toLowerCase()
        val imageFirstID = context.resources.getIdentifier(imgFirstString, "drawable", context.packageName)
        val imageSecondID = context.resources.getIdentifier(imgSecondString, "drawable", context.packageName)
        circleImage.setImage(view, imageFirstID, p0.firstFlag)
        circleImage.setImage(view, imageSecondID, p0.secondFlag)
        p0.firstCode.text = alertList[p1].currencyFirst
        p0.secondCode.text = alertList[p1].currencySecond
        p0.firstValue.text = decimalFormat.format(alertList[p1].firstValue.toDouble())
        p0.secondValue.text = decimalFormat.format(alertList[p1].secondValue.toDouble())


        if (alertList[p1].isAvailable) {
            p0.firstCode.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            p0.secondCode.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            p0.firstValue.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            p0.secondValue.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
        }

        p0.itemView.setOnClickListener {
            val intent = Intent(context, DetailController::class.java)
            intent.putExtra("alertName", alertList[p1].name)
            intent.putExtra("alertDate", alertList[p1].date)
            intent.putExtra("alertCheck", alertList[p1].isAvailable)
            val date = alertList[p1].year + "-" + alertList[p1].month + "-" + alertList[p1].day
            intent.putExtra("createdDate", date)
            context.startActivity(intent)
        }

        p0.itemView.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                dialogAlert.show(context, fragment,"Deleting Alert", "Are you sure you want to delete this Alert?", alertList[p1].name)
                return true
            }
        })
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val firstFlag = view.imgAlertFirstFlag
        val secondFlag = view.imgAlertSecondFlag
        val firstCode = view.txtAlertFirstCode
        val secondCode = view.txtAlertSecondCode
        val firstValue = view.txtAlertFirstValue
        val secondValue = view.txtAlertSecondValue
    }
}