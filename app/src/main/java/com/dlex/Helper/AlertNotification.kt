package com.dlex.Helper

import android.content.Context
import com.google.gson.GsonBuilder
import com.dlex.Model.Alert
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder
import java.text.DecimalFormat

class AlertNotification {

    fun alertUpdate(context: Context) {
        val internet = Internet
        val singleDate = SingleDate()
        var notificationID = 100
        val jsonUpdate = JsonUpdate(context)
        val standard= context.getSharedPreferences("mainCurrency", 0)
        val update = standard.edit()
        val notificationController = NotificationController()
        val alertList = mutableListOf<Alert>()
        val updateList = HashSet<String>()
        val gson = GsonBuilder().create()
        if (internet.isConnected(context)) {
            val body = standard.getStringSet("alertList", null)
            update.remove("alertList")
            update.apply()
            if (body != null) {
                for (i in body) {
                    val element = standard.getString(i, null)
                    alertList.add(gson.fromJson(element, Alert::class.java))
                }
                val apiKey = "replace-your-api-key"
                val firstUrl = "https://api.currencyconverterapi.com/api/v6/convert?q="
                val lastUrl = "&compact=ultra&apiKey=$apiKey"
                val stringBuilder = StringBuilder()
                for (j in 0..alertList.size - 1) {
                    val element = alertList[j]
                    stringBuilder.append(element.currencyFirst + "_" + element.currencySecond)
                    if (j != alertList.size - 1) {
                        stringBuilder.append(",")
                    }
                }
                val midUrl = stringBuilder.toString()
                val url = firstUrl + midUrl + lastUrl
                val request = Request.Builder().url(url).build()
                val cilent = OkHttpClient()
                cilent.newCall(request).enqueue(object: Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val data = response.body()?.string()
                        val jsonObject = JSONObject(data)
                        for (i in 0..alertList.size - 1) {
                            val currentElement = alertList[i]
                            val getKey = currentElement.currencyFirst + "_" + currentElement.currencySecond
                            val parseValue = jsonObject.get(getKey)
                            if (!currentElement.isAvailable && parseValue.toString().toDouble() >= currentElement.secondValue.toDouble()) {
                                val currentAlert = currentElement
                                currentAlert.date = singleDate.showDateDigit()
                                currentAlert.isAvailable = true
                                update.putString(currentAlert.name, jsonUpdate.createJson(currentAlert))
                                update.apply()
                                updateList.add(currentAlert.name)
                                val decimalFormat = DecimalFormat("###,###.#####")
                                val detail = "${currentAlert.currencyFirst} to ${currentAlert.currencySecond} at 1 = ${decimalFormat.format(currentAlert.secondValue.toDouble())} has been found"
                                notificationController.show(context, detail, notificationID)
                                notificationID += 1
                            } else {
                                update.putString(currentElement.name, jsonUpdate.createJson(currentElement))
                                update.apply()
                                updateList.add(currentElement.name)
                            }
                        }
                        update.putStringSet("alertList", updateList)
                        update.apply()
                    }
                    override fun onFailure(call: Call, e: IOException) {

                    }
                })
            }
        }
    }
}