package com.dlex.Helper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.dlex.Model.Alert
import com.dlex.Model.CurrencyData
import org.json.JSONObject
import java.lang.StringBuilder

class JsonUpdate(context: Context) {
    private val singleDate = SingleDate()
    private var loadJson = LoadJson
    private val json = loadJson.loadJsonFromAsset(context, "Json/CountryCurrency.json")!!
    private val gson = GsonBuilder().create()
    private val standard= context.getSharedPreferences("mainCurrency", 0)

    fun initializeNewJson(mainCurrency: String, date: String): String {
        val formatDate = singleDate.updatingDate(date)
        val stringBuilder = StringBuilder()
        stringBuilder.append("{")
        var body: String? = null
        var count = 0
        while (body == null) {
            body = standard.getString("tempSearch", null)
            count += 1
            if (body != null) {
                val jsonObject = JSONObject(body)
                for (j in 0..json.length() -1) {
                    val element = json.getJSONObject(j)
                    val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
                    if (!currency.code.equals(mainCurrency)) {
                        val key = mainCurrency + "_" + currency.code
                        stringBuilder.append('"')
                        stringBuilder.append(key)
                        stringBuilder.append('"')
                        stringBuilder.append(':')
                        val data = jsonObject.get(key) as JSONObject
                        val value = data.get(formatDate) as Double
                        stringBuilder.append(value)
                        val lastElement = json.getJSONObject(json.length() - 1).get("code")
                        if (j != json.length() -1) {
                            if (mainCurrency != lastElement) {
                                stringBuilder.append(",")
                            } else if (j != json.length() - 2) {
                                stringBuilder.append(",")
                            }
                        }
                    }
                }
                stringBuilder.append("}")
                return stringBuilder.toString()
            } else if (count == 10000000) {
                return ""
            }
        }
        return ""
    }

    fun createJson(data: Alert): String {
        val json = GsonBuilder().create()
        val jsonString = json.toJson(data)
        return jsonString
    }
}