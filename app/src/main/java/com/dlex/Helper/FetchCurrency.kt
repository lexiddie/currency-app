package com.dlex.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.GsonBuilder
import com.dlex.Model.Alert
import com.dlex.Model.CurrencyData
import com.dlex.Model.CurrencyValue
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class FetchCurrency(context: Context) {

    private var loadJson = LoadJson
    private val singleDate = SingleDate()
    private val gson = GsonBuilder().create()
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("yyyy MM dd")
    private val currentDate = dateFormat.format(Date())
    private val json = loadJson.loadJsonFromAsset(context, "Json/CountryCurrency.json")!!
    private val standard= context.getSharedPreferences("mainCurrency", 0)
    private lateinit var update: SharedPreferences.Editor


    @SuppressLint("CommitPrefEdits")
    fun initializeMemoryData(currencyType: String) {
        update = standard.edit()
        val check = standard.getString("updatedDate", null)
        if (check == null || check != currentDate) {
            initializeFetchData(currencyType)
            for (i in 0..json.length() - 1) {
                val element = json.getJSONObject(i)
                val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
                if (!currency.code.equals(currencyType)) {
                    initializeFetchData(currency.code)
                }
            }
        }
    }

    private fun initializeFetchData(currencyCode: String) {
        val apiKey = "replace-your-api-key"
        val firstUrl = "https://api.currencyconverterapi.com/api/v6/convert?q="
        val lastUrl = "&compact=ultra&apiKey=$apiKey"
        val stringBuilder = StringBuilder()
        for (j in 0..json.length() -1) {
            val element = json.getJSONObject(j)
            val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
            if (!currency.code.equals(currencyCode)) {
                stringBuilder.append(currencyCode + "_${currency.code}")
                val lastElement = json.getJSONObject(json.length() - 1).get("code")
                if (j != json.length() -1) {
                    if (currencyCode != lastElement) {
                        stringBuilder.append(",")
                    } else if (j != json.length() - 2) {
                        stringBuilder.append(",")
                    }
                }
            }
        }

        val midUrl = stringBuilder.toString()
        val url = firstUrl + midUrl + lastUrl
        val request = Request.Builder().url(url).build()
        val cilent = OkHttpClient()
        cilent.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val jsonObject = JSONObject(body)
                update.putString("updatedDate", currentDate)
                update.putString(currencyCode, jsonObject.toString())
                update.apply()
            }
            override fun onFailure(call: Call, e: IOException) {

            }
        })
    }

    fun initializeData(currencyType: String): MutableList<CurrencyValue> {
        val currencyList = mutableListOf<CurrencyValue>()
        var body: String? = null
        while (body == null) {
            body = standard.getString(currencyType, null)
            if (body != null) {
                val jsonObject = JSONObject(body)
                for (i in 0..json.length() - 1) {
                    val element = json.getJSONObject(i)
                    val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
                    if (!currency.code.equals(currencyType)) {
                        val key = currencyType + "_" + currency.code
                        val value = jsonObject.get(key)
                        currencyList.add(CurrencyValue(currency.currency, currency.code, value.toString().toDouble()))
                    }
                }
                return currencyList
            }
        }
        return currencyList
    }

    fun initializePair(firstCurrency: String, secondCurrency: String): Double {
        var body: String? = null
        while (body == null) {
            body = standard.getString(firstCurrency, null)
            if (body != null) {
                val jsonObject = JSONObject(body)
                val key = firstCurrency + "_" + secondCurrency
                val value = jsonObject.get(key) as Double
                return value
            }
        }
        return 0.0
    }

    fun initializePairHistory(firstCurrency: String, secondCurrency: String, subKey: String): Double {
        var body: String? = null
        while (body == null) {
            body = standard.getString(subKey, null)
            if (body != null) {
                val jsonObject = JSONObject(body)
                val key = firstCurrency + "_" + secondCurrency
                val value = jsonObject.get(key) as Double
                return value
            }
        }
        return 0.0
    }

    fun initializeSearch(currencyType: String, date: String) {
        val stringBuilder = StringBuilder()
        for (j in 0..json.length() -1) {
            val element = json.getJSONObject(j)
            val currency = gson.fromJson(element.toString(), CurrencyData::class.java)
            if (!currency.code.equals(currencyType)) {
                stringBuilder.append(currencyType + "_${currency.code}")
                val lastElement = json.getJSONObject(json.length() - 1).get("code")
                if (j != json.length() -1) {
                    if (currencyType != lastElement) {
                        stringBuilder.append(",")
                    } else if (j != json.length() - 2) {
                        stringBuilder.append(",")
                    }
                }
            }
        }
        val apiKey = "replace-your-api-key"
        val firstUrl = "https://api.currencyconverterapi.com/api/v6/convert?q="
        val secondUrl = stringBuilder.toString()
        val thirdUrl = "&compact=ultra&date="
        val fourthUrl = singleDate.updatingDate(date)
        val fifthUrl = "&apiKey=$apiKey"
        val url = firstUrl + secondUrl + thirdUrl + fourthUrl + fifthUrl
        Log.e("Url", url)
        val request = Request.Builder().url(url).build()
        val cilent = OkHttpClient()
        cilent.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val jsonObject = JSONObject(body)
                update.putString("tempSearch", jsonObject.toString())
                update.apply()
            }
            override fun onFailure(call: Call, e: IOException) {

            }
        })
    }

    fun initializeSearchData(firstCurrency: String, secondCurrency: String, date: String): Double {
        val formatDate = singleDate.updatingDate(date)
        var body: String? = null
        var count = 0
        while (body == null) {
            body = standard.getString("tempSearch", null)
            count += 1
            if (body != null) {
                val jsonObject = JSONObject(body)
                val key = firstCurrency + "_" + secondCurrency
                val element = jsonObject.get(key) as JSONObject
                val value = element.get(formatDate) as Double
                return value
            } else if (count == 10000000) {
                return -1.0
            }
        }
        return 0.0
    }

}